/**
 * Copyright 2019-2020 Wingify Software Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vwo.services.core;

import com.vwo.enums.CampaignEnums;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.enums.SegmentationTypeEnums;
import com.vwo.enums.StatusEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Variation;
import com.vwo.services.segmentation.PreSegmentation;
import com.vwo.services.segmentation.enums.VWOAttributesEnum;
import com.vwo.services.settings.SettingsFileUtil;
import com.vwo.services.storage.Storage;
import com.vwo.utils.CampaignUtils;
import com.vwo.utils.StorageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VariationDecider {

  private final BucketingService bucketingService;
  private final Storage.User userStorage;
  private static final Logger LOGGER = Logger.getLogger(VariationDecider.class);

  public VariationDecider(BucketingService bucketingService, Storage.User userStorage) {
    this.bucketingService = bucketingService;
    this.userStorage = userStorage;
  }

  /**
   * Determines the variation of a user for a campaign.
   *
   * @param campaign - campaign instance
   * @param userId   - user id string
   * @param rawCustomVariables Pre Segmentation custom variables
   * @param rawVariationTargetingVariables User Whitelisting Targeting variables
   * @return variation name or null if not found.
   */
  public Variation getVariation(Campaign campaign, String userId, Map<String, ?> rawCustomVariables, Map<String, ?> rawVariationTargetingVariables) {
    // Default initialization(s)
    final Map<String, ?> customVariables = rawCustomVariables == null ? new HashMap<>() : rawCustomVariables;
    final Map<String, ?> variationTargetingVariables = rawVariationTargetingVariables == null ? new HashMap<>() : rawVariationTargetingVariables;
    ((Map<String, Object>) variationTargetingVariables).put(VWOAttributesEnum.USER_ID.value(), userId);


    if (!campaign.getStatus().equalsIgnoreCase(CampaignEnums.STATUS.RUNNING.value())) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
        }
      }));

      return null;
    }

    Variation variation;

    if (campaign.getIsForcedVariationEnabled() == true) {
      List<Variation> whiteListedVariations = new ArrayList<>();
      campaign.getVariations().forEach(variationObj -> {
        if (variationObj.getSegments() == null || ((HashMap) variationObj.getSegments()).isEmpty()) {
          LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SEGMENTATION_SKIPPED.value(new HashMap<String, String>() {
            {
              put("userId", userId);
              put("campaignKey", campaign.getKey());
              put("variation", variationObj.getName());
            }
          }));
        } else {
          String status = StatusEnums.FAILED.value();
          if (PreSegmentation.isPresegmentValid(variationObj.getSegments(), variationTargetingVariables, userId, campaign.getKey())) {
            whiteListedVariations.add(variationObj.clone());
            status = StatusEnums.PASSED.value();
          }

          final String newStatus = status;
          LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SEGMENTATION_STATUS.value(new HashMap<String, String>() {
            {
              put("userId", userId);
              put("campaignKey", campaign.getKey());
              put("customVariables", variationTargetingVariables.toString());
              put("segmentationType", SegmentationTypeEnums.WHITELISTING.value());
              put("variation", variationObj.getName());
              put("status", newStatus);
            }
          }));
        }
      });

      if (whiteListedVariations.size() != 0) {
        Variation whiteListedVariation = whiteListedVariations.get(0);

        if (whiteListedVariations.size() > 1) {
          CampaignUtils.rationalizeVariationsWeights(whiteListedVariations);
          SettingsFileUtil.setVariationRange(whiteListedVariations);
          whiteListedVariation = bucketingService.getUserVariation(whiteListedVariations, campaign.getKey(), 100, userId);
        }

        this.setVariationInUserStorage(whiteListedVariation, campaign.getKey(), userId);

        String variationName = whiteListedVariation.getName();

        LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.SEGMENTATION_STATUS.value(new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", campaign.getKey());
            put("customVariables", variationTargetingVariables.toString());
            put("segmentationType", SegmentationTypeEnums.WHITELISTING.value());
            put("variation", variationName);
            put("status", StatusEnums.PASSED.value());
          }
        }));

        return whiteListedVariation;
      } else {
        LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.SEGMENTATION_STATUS.value(new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", campaign.getKey());
            put("customVariables", variationTargetingVariables.toString());
            put("segmentationType", SegmentationTypeEnums.WHITELISTING.value());
            put("variation", "");
            put("status", StatusEnums.FAILED.value());
          }
        }));
      }
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.WHITELISTING_SKIPPED.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
        }
      }));
    }

    // Try to lookup in user storage service to get variation.
    if (this.userStorage != null) {
      try {
        Map<String, String> userStorageMap = this.userStorage.get(userId, campaign.getKey());

        if (userStorageMap == null) {
          LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.NO_DATA_USER_STORAGE_SERVICE.value());
        } else if (StorageUtils.isValidUserStorageMap(userStorageMap)) {
          variation = getStoredVariation(userStorageMap, userId, campaign);

          if (variation != null) {
            String variationName = variation.getName();
            LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.GOT_STORED_VARIATION.value(new HashMap<String, String>() {
              {
                put("variationName", variationName);
                put("campaignKey", campaign.getKey());
                put("userId", userId);
              }
            }));
            return variation;
          } else {
            LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.NO_STORED_VARIATION.value(new HashMap<String, String>() {
              {
                put("campaignKey", campaign.getKey());
                put("userId", userId);
              }
            }));
          }
        } else {
          LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.INVALID_USER_STORAGE_MAP.value(new HashMap<String, String>() {
            {
              put("map", userStorageMap.toString());
            }
          }));
        }
      } catch (Exception e) {
        LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.NO_DATA_IN_USER_STORAGE.value());
      }
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.NO_USER_STORAGE_DEFINED.value());
    }

    // Check if user satisfies pre segmentation. If not, return null.
    if (campaign.getSegments() != null && !((LinkedHashMap) campaign.getSegments()).isEmpty()) {
      boolean isPresegmentValid = PreSegmentation.isPresegmentValid(campaign.getSegments(), customVariables, userId, campaign.getKey());
      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.SEGMENTATION_STATUS.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaign.getKey());
          put("customVariables", customVariables.toString());
          put("segmentationType", SegmentationTypeEnums.PRE_SEGMENTATION.value());
          put("variation", "(No variation i.e. Presegment)");
          put("status", isPresegmentValid ? StatusEnums.PASSED.value() : StatusEnums.FAILED.value());
        }
      }));

      if (!isPresegmentValid) {
        return null;
      }
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SEGMENTATION_SKIPPED.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("variation", "(No variation i.e. Presegment)");
        }
      }));
    }

    // Get variation using campaign settings for a user.
    variation = bucketingService.getUserVariation(campaign.getVariations(), campaign.getKey(), campaign.getPercentTraffic(), userId);
    this.setVariationInUserStorage(variation, campaign.getKey(), userId);
    return variation;
  }

  /**
   * Fetch the variation info of a user for a campaign from user defined user storage service.
   *
   * @param userStorageMap - User storage service hash map
   * @param userId         - user ID
   * @param campaign       - campaign instance
   * @return               - stored variation name
   */
  private Variation getStoredVariation(Map<String, String> userStorageMap, String userId, Campaign campaign) {

    String userIdFromMap = userStorageMap.get(Storage.User.userId);
    String campaignKey = userStorageMap.get(Storage.User.campaignKey);
    String variationName = userStorageMap.get(Storage.User.variationKey);

    if ((!userIdFromMap.equalsIgnoreCase(userId) && !campaignKey.equalsIgnoreCase(campaign.getKey())) || variationName == null) {
      return null;
    }

    for (Variation variation : campaign.getVariations()) {
      if (variation.getName().equalsIgnoreCase(variationName)) {
        return variation;
      }
    }
    return null;
  }

  /**
   * The function saveVariation has references from "Optimizely Java SDK, version 3.2.0", Copyright 2017-2019, Optimizely,
   * under Apache 2.0 License.
   * Source - https://github.com/optimizely/java-sdk/blob/master/core-api/src/main/java/com/optimizely/ab/bucketing/DecisionService.java
   */

  /**
   * Set variation info if user storage service is provided the the user.
   *
   * @param userId    - user id
   * @param campaignKey  - campaign key
   * @param variation - variation instance
   */
  private void setVariation(String userId, String campaignKey, Variation variation) {
    if (this.userStorage != null) {
      String campaignId = campaignKey;
      String variationId = variation.getName();

      Map<String, String> variationMap = new HashMap<String, String>() {
        {
          put(Storage.User.userId, userId);
          put(Storage.User.campaignKey, campaignId);
          put(Storage.User.variationKey, variationId);
        }
      };

      try {
        this.userStorage.set(variationMap);
      } catch (Exception e) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.SAVE_USER_STORAGE_SERVICE_FAILED.value(new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", campaignKey);
          }
        }), e.getStackTrace());
      }
    }
  }

  /**
   * Store variation info in user storage, if available.
   * @param variation - variation instance
   * @param campaignKey - campaign key
   * @param userId - user id
   */
  private void setVariationInUserStorage(Variation variation, String campaignKey, String userId) {
    // Set variation in user storage service if defined by the customer.
    if (variation != null) {
      String variationName = variation.getName();
      if (this.userStorage != null) {
        setVariation(userId, campaignKey, variation);
        LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SAVED_IN_USER_STORAGE_SERVICE.value(new HashMap<String, String>() {
          {
            put("userId", userId);
            put("variation", variationName);
          }
        }));
      }
      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.GOT_VARIATION_FOR_USER.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
          put("variation", variationName);
        }
      }));
    } else {
      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.USER_NOT_PART_OF_CAMPAIGN.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
        }
      }));
    }
  }
}
