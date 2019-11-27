/**
 * Copyright 2019 Wingify Software Pvt. Ltd.
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
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Variation;
import com.vwo.services.storage.Storage;
import com.vwo.utils.StorageUtils;

import java.util.HashMap;
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
   * The function getVariation has references from "Optimizely Java SDK, version 3.2.0", Copyright 2017-2019, Optimizely,
   * under Apache 2.0 License.
   * Source - https://github.com/optimizely/java-sdk/blob/master/core-api/src/main/java/com/optimizely/ab/bucketing/DecisionService.java
   */

  /**
   * Determines the variation of a user for a campaign.
   *
   * @param campaign - campaign instance
   * @param userId   - user id string
   * @return variation name or null if not found.
   */
  public Variation getVariation(Campaign campaign, String userId) {
    if (!campaign.getStatus().equalsIgnoreCase(CampaignEnums.STATUS.RUNNING.value())) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_RUNNING.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
        }
      }));

      return null;
    }

    Variation variation;

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

    // Get variation using campaign settings for a user.
    variation = bucketingService.getUserVariation(campaign, userId);

    // Set variation in user storage service if defined by the customer.
    if (variation != null) {
      String variationName = variation.getName();
      if (this.userStorage != null) {
        setVariation(userId, campaign, variation);
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
          put("campaignKey", campaign.getKey());
          put("variation", variationName);
        }
      }));
    } else {
      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.USER_NOT_PART_OF_CAMPAIGN.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaign.getKey());
        }
      }));
    }

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
   * @param campaign  - campaign instance
   * @param variation - variation instance
   */
  private void setVariation(String userId, Campaign campaign, Variation variation) {
    if (this.userStorage != null) {
      String campaignId = campaign.getKey();
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
            put("campaignKey", campaign.getKey());
          }
        }), e.getStackTrace());
      }
    }
  }
}