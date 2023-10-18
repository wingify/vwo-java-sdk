/**
 * Copyright 2019-2022 Wingify Software Pvt. Ltd.
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
import com.vwo.enums.APIEnums;
import com.vwo.enums.SegmentationTypeEnums;
import com.vwo.enums.StatusEnums;
import com.vwo.enums.HooksEnum;
import com.vwo.enums.UriEnums;
import com.vwo.logger.Logger;
import com.vwo.logger.LoggerService;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Goal;
import com.vwo.models.response.Settings;
import com.vwo.models.response.Variation;
import com.vwo.services.api.TrackCampaign;
import com.vwo.services.integrations.HooksManager;
import com.vwo.services.segmentation.PreSegmentation;
import com.vwo.services.segmentation.enums.VWOAttributesEnum;
import com.vwo.services.settings.SettingsFileUtil;
import com.vwo.services.storage.Storage;
import com.vwo.services.storage.UserStorage;
import com.vwo.utils.CampaignUtils;
import com.vwo.utils.StorageUtils;
import com.vwo.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VariationDecider {

  private final BucketingService bucketingService;
  private final Storage.User userStorage;
  public boolean isStoredVariation;
  private final HooksManager hooksManager;
  private final int accountId;
  private Map<String, Object> integrationsMap;
  private static final Logger LOGGER = Logger.getLogger(VariationDecider.class);

  // MEG related
  public static final int ALGO_RANDOM = 1;
  public static final int ALGO_ADVANCED = 2;

  public VariationDecider(BucketingService bucketingService, Storage.User userStorage,
                          HooksManager hooksManager, int accountId) {
    this.bucketingService = bucketingService;
    this.userStorage = userStorage;
    this.hooksManager = hooksManager;
    this.accountId = accountId;
  }



  public boolean getIsStoredVariation() {
    return isStoredVariation;
  }

  /**
   * Determines the variation of a user for a campaign.
   *
   * @param settings                       - settings instance
   * @param apiName                        -  name of the API
   * @param campaign                       - campaign instance
   * @param userId                         - user id string
   * @param rawCustomVariables             Pre Segmentation custom variables
   * @param rawVariationTargetingVariables User Whitelisting Targeting variables
   * @param goalIdentifier                 Goal key
   * @return variation name or null if not found.
   */
  public Variation getVariation(
          Settings settings,
          String apiName,
          Campaign campaign,
          String userId,
          Map<String, ?> rawCustomVariables,
          Map<String, ?> rawVariationTargetingVariables,
          String goalIdentifier
  ) {
    // Default initialization(s)
    isStoredVariation = false;
    integrationsMap = new HashMap<String, Object>();
    String uuid = UUIDUtils.getUUId(settings.getAccountId(), userId);
    initIntegrationMap(campaign, apiName, userId, goalIdentifier, rawCustomVariables, rawVariationTargetingVariables);
    final Map<String, ?> customVariables = rawCustomVariables == null ? new HashMap<>() : rawCustomVariables;
    final Map<String, ?> variationTargetingVariables = rawVariationTargetingVariables == null ? new HashMap<>() : rawVariationTargetingVariables;
    ((Map<String, Object>) variationTargetingVariables).put(VWOAttributesEnum.USER_ID.value(), campaign.isUserListEnabled()
        ? uuid
        : userId);

    Map<String, List<Campaign>> processedCampaigns = null;

    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("USER_UUID"), new HashMap<String, String>() {
      {
        put("accountId", String.valueOf(settings.getAccountId()));
        put("userId", userId);
        put("uuid", uuid);
      }
    }));

    if (!campaign.getStatus().equalsIgnoreCase(CampaignEnums.STATUS.RUNNING.value())) {
      LOGGER.warn(LoggerService.getComputedMsg(LoggerService.getInstance().warningMessages.get("CAMPAIGN_NOT_RUNNING"), new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("api", apiName);
        }
      }));

      return null;
    }

    Variation variation;

    // check if campaign is part of an MEG
    Map<String, Object> groupDetails = CampaignUtils.isPartOfGroup(settings, campaign.getId());
    if (!groupDetails.isEmpty()) {
      integrationsMap.put("groupId", groupDetails.get("groupId"));
      integrationsMap.put("groupName", groupDetails.get("groupName"));
    }

    Variation whitelistedVariation = checkForWhitelisting(campaign, userId, 
        variationTargetingVariables, false, settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId());

    if (whitelistedVariation != null) {
      return whitelistedVariation;
    }

    if (campaign.getIsAlwaysCheckSegment()) {
      Boolean isPreSegmentationValid = checkForPreSegmentation(campaign, userId, customVariables,
          false);
      if (isPreSegmentationValid) {
        return evaluateTrafficAndGetVariation(campaign, userId, goalIdentifier, settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId());
      } else {
        return null;
      }
    }

    Variation userVariation = checkForUserStorage(apiName, campaign, userId, goalIdentifier, false);
    if (userVariation == null || userVariation.getId() != -1) {
      return userVariation;
    }

    // Check if user satisfies pre segmentation. If not, return null.
    Boolean isPreSegmentationValid = checkForPreSegmentation(campaign, userId, customVariables,
        false);
    if (!(isPreSegmentationValid && BucketingService.getUserHashForCampaign(CampaignUtils
        .getBucketingSeed(userId, campaign, null, settings.getIsNB()),
            campaign, userId, campaign.getPercentTraffic(), true) != -1)) {
      return null;
    }

    // THIS IS WHERE MEG IMPLEMENTATION IS HAPPENING!!!
    if (groupDetails.containsKey("groupId")) {
      List<Campaign> campaignList = CampaignUtils.getGroupCampaigns(settings,
          (int) groupDetails.get("groupId"));

      if (campaignList.isEmpty()) {
        return null;
      }

      if (checkForStorageAndWhitelisting(apiName, campaignList,
          (String) groupDetails.get("groupName"), campaign, userId, goalIdentifier, 
          variationTargetingVariables, settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId())) {
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages
            .get("MEG_CALLED_CAMPAIGN_NOT_WINNER"), new HashMap<String, String>() {
              {
                put("userId", userId);
                put("campaignKey", campaign.getKey());
                put("groupName", String.valueOf(groupDetails.get("groupName")));
              }
            }));
        return null;
      }

      // get eligible campaigns
      processedCampaigns = getEligibleCampaigns(campaignList, userId, customVariables, settings.getIsNB());
      final int numEligibleCampaigns = processedCampaigns.get("eligibleCampaigns").size();

      StringBuilder eligibleCampaignKeys = new StringBuilder();
      for (Campaign eachCampaign : processedCampaigns.get("eligibleCampaigns")) {
        eligibleCampaignKeys.append(eachCampaign.getKey()).append(", ");
      }

      StringBuilder inEligibleCampaignKeys = new StringBuilder();
      for (Campaign eachCampaign : processedCampaigns.get("inEligibleCampaigns")) {
        inEligibleCampaignKeys.append(eachCampaign.getKey()).append(", ");
      }

      String finalInEligibleCampaignKeys = inEligibleCampaignKeys.toString();
      String finalEligibleCampaignKeys = eligibleCampaignKeys.toString();
      LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("MEG_ELIGIBLE_CAMPAIGNS"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("groupName", (String) groupDetails.get("groupName"));
          put("eligibleCampaignKeys", eligibleCampaignKeys.substring(0, eligibleCampaignKeys.length() - 2));
          put("inEligibleText", finalInEligibleCampaignKeys.isEmpty() ? "no campaigns" : "campaigns: " + inEligibleCampaignKeys.substring(0, inEligibleCampaignKeys.length() - 2));
        }
      }));

      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("MEG_ELIGIBLE_CAMPAIGNS"), new HashMap<String, String>() {
        {
          put("userId", userId);
          // put("noOfEligibleCampaigns", String.valueOf(processedCampaigns.get("eligibleCampaigns").size()));
          put("noOfEligibleCampaigns", String.valueOf(numEligibleCampaigns));
          put("noOfGroupCampaigns", String.valueOf(campaignList.size()));
          put("groupName", (String) groupDetails.get("groupName"));
        }
      }));

      if (processedCampaigns.get("eligibleCampaigns").size() == 1) {
        return evaluateTrafficAndGetVariation(processedCampaigns.get("eligibleCampaigns").get(0), userId, goalIdentifier, settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId());
      } else {
        // based on algo, find winning campaign and get variation
        switch ((int) groupDetails.get("algorithm")) {
          case ALGO_RANDOM:
            return normalizeAndFindWinningCampaign(processedCampaigns.get("eligibleCampaigns"),
                campaign, userId, goalIdentifier, (String) groupDetails.get("groupName"),
                (int) groupDetails.get("groupId"), settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId());

          case ALGO_ADVANCED:
            return advancedFindWinningCampaign(processedCampaigns.get("eligibleCampaigns"),
                campaign, userId, goalIdentifier, (String) groupDetails.get("groupName"),
                (int) groupDetails.get("groupId"), settings);

          default:
            return null;
        }
      }
    } else {
      return evaluateTrafficAndGetVariation(campaign, userId, goalIdentifier, settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId());
    }
  }

  /**
   * Evaluate a campaign for whitelisting.
   *
   * @param campaign                    - Campaign instance
   * @param userId                      - user id string
   * @param variationTargetingVariables - User Whitelisting Targeting variables
   * @return whitelisted variation.
   */
  private Variation checkForWhitelisting(Campaign campaign, String userId,
      Map<String, ?> variationTargetingVariables, boolean disableLogs,
      boolean isNewBucketingEnabled, boolean isNewBucketingEnabledv2, Integer accountId) {
    if (campaign.getIsForcedVariationEnabled() == true) {
      List<Variation> whiteListedVariations = new ArrayList<>();
      campaign.getVariations().forEach(variationObj -> {
        if (variationObj.getSegments() == null || ((HashMap) variationObj.getSegments()).isEmpty()) {
          LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("SEGMENTATION_SKIPPED"), new HashMap<String, String>() {
            {
              put("userId", userId);
              put("campaignKey", campaign.getKey());
              put("variation", campaign.getType().equals(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value()) ? "" : "for " + variationObj.getName());
            }
          }), disableLogs);
        } else {
          String status = StatusEnums.FAILED.value();
          if (PreSegmentation.isPresegmentValid(variationObj.getSegments(), variationTargetingVariables, userId, campaign.getKey())) {
            whiteListedVariations.add(variationObj.clone());
            status = StatusEnums.PASSED.value();
          }

          final String newStatus = status;
          LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("SEGMENTATION_STATUS"), new HashMap<String, String>() {
            {
              put("userId", userId);
              put("campaignKey", campaign.getKey());
              put("customVariables", variationTargetingVariables.toString());
              put("segmentationType", SegmentationTypeEnums.WHITELISTING.value());
              put("variation", campaign.getType().equals(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())
                  && newStatus.equals(StatusEnums.PASSED.value()) ? "and becomes part of the rollout" : "for " + variationObj.getName());
              put("status", newStatus);
            }
          }), disableLogs);
        }
      });

      if (whiteListedVariations.size() != 0) {
        Variation whiteListedVariation = whiteListedVariations.get(0);

        if (whiteListedVariations.size() > 1) {
          CampaignUtils.rationalizeVariationsWeights(whiteListedVariations);
          SettingsFileUtil.setVariationRange(campaign, whiteListedVariations);
          whiteListedVariation = (Variation) bucketingService.getUserVariation(whiteListedVariations, campaign, 100, userId, isNewBucketingEnabled, isNewBucketingEnabledv2, accountId);
        }

        // this.setVariationInUserStorage(whiteListedVariation, campaign.getKey(), userId);

        String variationName = whiteListedVariation.getName();

        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("SEGMENTATION_STATUS"), new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", campaign.getKey());
            put("customVariables", variationTargetingVariables.toString());
            put("segmentationType", SegmentationTypeEnums.WHITELISTING.value());
            put("variation", campaign.getType().equals(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value()) ? "" : "for " + variationName);
            put("status", StatusEnums.PASSED.value());
          }
        }), disableLogs);
        if (!disableLogs) {
          executeIntegrationsCallback(false, campaign, whiteListedVariation, true);
        }
        return whiteListedVariation;
      } else {
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("SEGMENTATION_STATUS"), new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", campaign.getKey());
            put("customVariables", variationTargetingVariables.toString());
            put("segmentationType", SegmentationTypeEnums.WHITELISTING.value());
            put("variation", "");
            put("status", StatusEnums.FAILED.value());
          }
        }), disableLogs);
      }
      return null;
    } else {
      LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("WHITELISTING_SKIPPED"), new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
        }
      }), disableLogs);
    }
    return null;
  }


  /**
   * Check if the variation is present in the user storage.
   *
   * @param apiName                  - name of the API
   * @param campaign                 - campaign instance
   * @param userId                   - user id string
   * @param goalIdentifier           - Goal key
   * @return Stored variation.
   */
  private Variation checkForUserStorage(String apiName, Campaign campaign, String userId, String goalIdentifier, boolean disableLogs) {
    Variation variation = new Variation();
    variation.setId(-1);
    // Try to lookup in user storage service to get variation.
    if (this.userStorage != null) {
      try {
        Map<String, String> userStorageMap = this.userStorage.get(userId, campaign.getKey());

        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("GETTING_DATA_USER_STORAGE_SERVICE"), new HashMap<String, String>() {
          {
            put("campaignKey", campaign.getKey());
            put("userId", userId);
          }
        }), disableLogs);

        if (userStorageMap == null) {
          if (!isCampaignActivated(apiName, userId, campaign)) {
            return null;
          } else {
            LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("USER_STORAGE_SERVICE_NO_STORED_DATA"), new HashMap<String, String>() {
              {
                put("campaignKey", campaign.getKey());
                put("userId", userId);
              }
            }), disableLogs);
          }
        } else if (StorageUtils.isValidUserStorageMap(userStorageMap)) {


          if (goalIdentifier != null) {
            if (checkGoalTracked(goalIdentifier, userStorageMap) && !checkMCA(goalIdentifier, campaign) && !checkHasProps(goalIdentifier, campaign)) {
              LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("CAMPAIGN_GOAL_ALREADY_TRACKED"), new HashMap<String, String>() {
                {
                  put("goalIdentifier", goalIdentifier);
                  put("campaignKey", campaign.getKey());
                  put("userId", userId);
                }
              }), disableLogs);
              return null;
            }
          }



          variation = getStoredVariation(userStorageMap, userId, campaign);

          if (variation != null) {
            String variationName = variation.getName();
            LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("GOT_STORED_VARIATION"), new HashMap<String, String>() {
              {
                put("variationName", variationName);
                put("campaignKey", campaign.getKey());
                put("userId", userId);
              }
            }), disableLogs);

            if (goalIdentifier != null) {
              setGoalInUserStorage(userStorageMap, goalIdentifier, userId, campaign);
            }
            isStoredVariation = true;
            if (!disableLogs) {
              executeIntegrationsCallback(true, campaign, variation, false);
            }
            return variation;
          } else {
            if (!isCampaignActivated(apiName, userId, campaign)) {
              return null;
            } else {
              LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("USER_STORAGE_SERVICE_NO_STORED_DATA"), new HashMap<String, String>() {
                {
                  put("campaignKey", campaign.getKey());
                  put("userId", userId);
                }
              }), disableLogs);
            }
          }
        } else {
          if (!isCampaignActivated(apiName, userId, campaign)) {
            return null;
          } else {
            LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("USER_STORAGE_SERVICE_NO_STORED_DATA"), new HashMap<String, String>() {
              {
                put("campaignKey", campaign.getKey());
                put("userId", userId);
              }
            }), disableLogs);
          }
        }
      } catch (Exception e) {
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("USER_STORAGE_SERVICE_GET_FAILED"), new HashMap<String, String>() {
          {
            put("userId", userId);
            put("error", e.getLocalizedMessage());
          }
        }), disableLogs);
      }
    } else {
      LOGGER.debug(LoggerService.getInstance().debugMessages.get("USER_STORAGE_SERVICE_NOT_CONFIGURED"), disableLogs);
    }
    return variation;
  }

  /**
   * Evaluate a campaign for pre-segmentation.
   *
   * @param campaign        - Campaign instance
   * @param userId          - user id string
   * @param customVariables - Pre Segmentation custom variables
   * @return true, if the pre-segmentation is satisfied.
   */
  private Boolean checkForPreSegmentation(Campaign campaign, String userId, Map<String, ?> customVariables, boolean disableLogs) {
    if (campaign.getSegments() != null && !((LinkedHashMap) campaign.getSegments()).isEmpty()) {
      boolean isPresegmentValid = PreSegmentation.isPresegmentValid(campaign.getSegments(), customVariables, userId, campaign.getKey());
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("SEGMENTATION_STATUS"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaign.getKey());
          put("customVariables", customVariables.toString());
          put("segmentationType", SegmentationTypeEnums.PRE_SEGMENTATION.value());
          put("variation", "");
          put("status", isPresegmentValid ? StatusEnums.PASSED.value() : StatusEnums.FAILED.value());
        }
      }), disableLogs);

      return isPresegmentValid;
    } else {
      LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("SEGMENTATION_SKIPPED"), new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("variation", "");
        }
      }), disableLogs);
      return true;
    }
  }

  /**
   * Evaluate the campaign for whitelisting and store.
   * This method would be called only for MEG campaigns.
   *
   * @param apiName                     - name of the API
   * @param campaignList                - List of campaigns to be evaluated
   * @param calledCampaign              - Campaign instance of called campaign
   * @param userId                      - user id string
   * @param goalIdentifier              - Goal key
   * @param variationTargetingVariables - User Whitelisting Targeting variables
   * @return true, if whitelisting/storage is satisfied for any campaign.
   */
  private boolean checkForStorageAndWhitelisting(String apiName, List<Campaign> campaignList, String groupName, Campaign calledCampaign, String userId, String goalIdentifier,
                                                 Map<String, ?> variationTargetingVariables, boolean isNewBucketingEnabled, boolean isNewBucketingEnabledV2, Integer accountId) {
    boolean otherCampaignWinner = false;
    for (Campaign campaign : campaignList) {
      if (campaign.getId().equals(calledCampaign.getId())) {
        continue;
      }
      Variation whitelistedVariation = checkForWhitelisting(campaign, userId, variationTargetingVariables, true, isNewBucketingEnabled, isNewBucketingEnabledV2, accountId);
      if (whitelistedVariation != null) {
        otherCampaignWinner = true;
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("OTHER_CAMPAIGN_SATISFIES_WHITELISTING_STORAGE"), new HashMap<String, String>() {
          {
            put("campaignKey", campaign.getKey());
            put("userId", userId);
            put("groupName", groupName);
            put("type", "whitelisting");
          }
        }));
        break;
      }

      Variation storedVariation = checkForUserStorage(apiName, campaign, userId, goalIdentifier, true);
      if (storedVariation != null && storedVariation.getId() != -1) {
        otherCampaignWinner = true;
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("OTHER_CAMPAIGN_SATISFIES_WHITELISTING_STORAGE"), new HashMap<String, String>() {
          {
            put("campaignKey", campaign.getKey());
            put("userId", userId);
            put("groupName", groupName);
            put("type", "user storage");
          }
        }));
        break;
      }
    }
    return otherCampaignWinner;
  }

  /**
   * Evaluate the list of campaigns for pre-segmentation and campaign traffic allocation and assign variation to the user.
   * This method will be used for MEG campaigns.
   *
   * @param campaignList    - List of campaigns to be evaluated
   * @param userId          - user id string
   * @param customVariables -  Pre Segmentation custom variables
   * @return List of campaigns which satisfies the conditions.
   */
  private Map<String, List<Campaign>> getEligibleCampaigns(List<Campaign> campaignList, String userId, Map<String, ?> customVariables, boolean isNewBucketingEnabled) {
    List<Campaign> eligibleCampaigns = new ArrayList<>();
    List<Campaign> inEligibleCampaigns = new ArrayList<>();
    for (Campaign campaign: campaignList) {
      if (checkForPreSegmentation(campaign, userId, customVariables, true)
              && BucketingService.getUserHashForCampaign(CampaignUtils.getBucketingSeed(userId, campaign, null, isNewBucketingEnabled), campaign, userId, campaign.getPercentTraffic(), true) != -1) {
        eligibleCampaigns.add(campaign.clone());
      } else {
        inEligibleCampaigns.add(campaign);
      }
    }

    return new HashMap<String, List<Campaign>>() {
      {
        put("eligibleCampaigns", eligibleCampaigns);
        put("inEligibleCampaigns", inEligibleCampaigns);
      }
    };
  }

  /**
   * Check if user is eligible for the camapign based on traffic percentage and assign variation.
   *
   * @param campaign       - Campaign instance
   * @param userId         - user id string
   * @param goalIdentifier - Goal key
   * @return variation assigned to the user.
   */
  private Variation evaluateTrafficAndGetVariation(Campaign campaign, String userId, String goalIdentifier, boolean isNewBucketingEnabled, boolean isNewBucketingEnabledV2, Integer accountId) {
    // Get variation using campaign settings for a user.
    Variation variation = null;
    variation = (Variation) bucketingService.getUserVariation(campaign.getVariations(), campaign, campaign.getPercentTraffic(), userId, isNewBucketingEnabled, isNewBucketingEnabledV2, accountId);
    Variation finalVariation = variation;
    LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("USER_VARIATION_ALLOCATION_STATUS"), new HashMap<String, String>() {
      {
        put("userId", userId);
        put("campaignKey", campaign.getKey());
        put("status", finalVariation != null ? "got variation:" + finalVariation.getName() : "did not get any variation");
      }
    }));
    if (variation != null) {
      this.setVariationInUserStorage(variation, campaign.getKey(), userId, goalIdentifier);
      executeIntegrationsCallback(false, campaign, variation, false);
    }

    return variation;
  }

  /**
   * Equally distribute the traffic of campaigns and assign a winner campaign by murmur hash.
   *
   * @param shortlistedCampaigns - List of eligible campaigns
   * @param calledCampaign       - Campaign instance of called campaign
   * @param userId               - user id string
   * @param goalIdentifier       - Goal Key
   * @param groupName            - Name of the group
   * @return variation of the winner campaign.
   */
  private Variation normalizeAndFindWinningCampaign(List<Campaign> shortlistedCampaigns,
      Campaign calledCampaign, String userId, String goalIdentifier, String groupName, int groupId,
      boolean isNewBucketingEnabled, boolean isNewBucketingEnabledV2, Integer accountId) {

    for (Campaign campaign : shortlistedCampaigns) {
      campaign.setWeight((double) (100 / shortlistedCampaigns.size()));
    }
    CampaignUtils.setCampaignRange(shortlistedCampaigns);
    Long bucketHash = BucketingService.getUserHashForCampaign(CampaignUtils.getBucketingSeed(userId, null, groupId, isNewBucketingEnabled), calledCampaign, userId, 100, true);
    int variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
    Campaign winnerCampaign = (Campaign) bucketingService.getAllocatedItem(shortlistedCampaigns, variationHashValue);

    LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("MEG_GOT_WINNER_CAMPAIGN"), new HashMap<String, String>() {
      {
        put("userId", userId);
        put("campaignKey", winnerCampaign.getKey());
        put("groupName", groupName);
      }
    }));

    if (winnerCampaign.getId().equals(calledCampaign.getId())) {
      return evaluateTrafficAndGetVariation(winnerCampaign, userId, goalIdentifier, isNewBucketingEnabled, isNewBucketingEnabledV2, accountId);
    } else {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("MEG_CALLED_CAMPAIGN_NOT_WINNER"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", calledCampaign.getKey());
          put("groupName", groupName);
        }
      }));
    }
    return null;
  }

  /**
   * Assign a winner campaign based on priority and traffic distribution.
   *
   * @param shortlistedCampaigns - List of eligible campaigns
   * @param calledCampaign       - Campaign instance of called campaign
   * @param userId               - user id string
   * @param goalIdentifier       - Goal Key
   * @param groupName            - Name of the group
   * @return variation of the winner campaign.
   */
  private Variation advancedFindWinningCampaign(List<Campaign> shortlistedCampaigns,
      Campaign calledCampaign, String userId, String goalIdentifier, String groupName, int groupId,
      Settings settings) {
    Campaign winnerCampaign = null;

    // priority campaigns
    List<Integer> priorityCampaigns = settings.getGroups().get(toString().valueOf(groupId)).getP();

    // traffic weightage campaigns
    Map<String, Integer> trafficWeightageCampaigns = settings.getGroups().get(toString()
        .valueOf(groupId)).getWt();
    List<Campaign> eligibleTrafficWeightageCampaigns = new ArrayList<>();
    List<Integer> percentageTrafficForWeightageCampaigns = new ArrayList<>();

    // parse through the priority campaigns and find winner from shortlisted campaigns
    for (int priorityCampaignId : priorityCampaigns) {
      // stop parsing if winner found
      if (winnerCampaign != null) {
        break;
      }

      // parse through the shortlisted campaigns to search for this priority campaign
      for (Campaign shortlistedCampaign : shortlistedCampaigns) {
        if (shortlistedCampaign.getId() == priorityCampaignId) {
          // set winner campaign
          winnerCampaign = shortlistedCampaign;
          final String winnerCampaignKey = winnerCampaign.getKey();

          LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages
              .get("MEG_GOT_WINNER_CAMPAIGN"), new HashMap<String, String>() {
                {
                  put("userId", userId);
                  put("campaignKey", winnerCampaignKey);
                  put("groupName", groupName);
                }
              }));

          break;
        }
      }
    }

    // if winner not found, parse through traffic weightage campaigns
    if (winnerCampaign == null) {
      // parse through shortlisted campaigns and get their traffic weightages
      for (Campaign campaign : shortlistedCampaigns) {
        if (trafficWeightageCampaigns.containsKey(toString().valueOf(campaign.getId()))) {
          // get campaign and percentage traffic
          eligibleTrafficWeightageCampaigns.add(campaign);
          percentageTrafficForWeightageCampaigns.add(trafficWeightageCampaigns.get(toString()
              .valueOf(campaign.getId())));
        }
      }

      // select winner based on traffic weightage, from shortlisted campaigns
      if (!eligibleTrafficWeightageCampaigns.isEmpty()) {
        winnerCampaign = getCampaignBasedOnTrafficWeightage(eligibleTrafficWeightageCampaigns,
            percentageTrafficForWeightageCampaigns);
        final String winnerCampaignKey = winnerCampaign.getKey();

        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages
            .get("MEG_GOT_WINNER_CAMPAIGN"), new HashMap<String, String>() {
              {
                put("userId", userId);
                put("campaignKey", winnerCampaignKey);
                put("groupName", groupName);
              }
            }));
      }
    }

    // return variation if winner is called campaign
    if (winnerCampaign.getId().equals(calledCampaign.getId())) {
      return evaluateTrafficAndGetVariation(winnerCampaign, userId, goalIdentifier, settings.getIsNB(), settings.getIsNBv2(), settings.getAccountId());
    } else {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages
          .get("MEG_CALLED_CAMPAIGN_NOT_WINNER"), new HashMap<String, String>() {
            {
              put("userId", userId);
              put("campaignKey", calledCampaign.getKey());
              put("groupName", groupName);
            }
          }));
    }
    return null;
  }

  /**
   * Get a winner campaign based on traffic weightage of campaigns.
   *
   * @param campaigns        - List of shortlisted campaigns
   * @param trafficWeightage - Corresponding traffic weightage of the campaigns
   * @return selected campaign based on random weightage of traffic
   */
  public static Campaign getCampaignBasedOnTrafficWeightage(List<Campaign> campaigns,
      List<Integer> trafficWeightage) {
    List<Integer> cumulativeWeights = new ArrayList<>();
    int sum = 0;

    // get the cumulative weights for the campaigns
    for (int weight : trafficWeightage) {
      sum += weight;
      cumulativeWeights.add(sum);
    }

    // get a weighted random
    int randomNum = (int) (Math.random() * sum);
    int index = Collections.binarySearch(cumulativeWeights, randomNum);

    // binarysearch returns a negative value when generated number is between cumulative weights
    if (index < 0) {
      index = -(index + 1);
    }

    return campaigns.get(index);
  }

  /**
   * Fetch the variation info of a user for a campaign from user defined user storage service.
   *
   * @param userStorageMap - User storage service hash map
   * @param userId         - user ID
   * @param campaign       - campaign instance
   * @return - stored variation name
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
   * @param userId      - user id
   * @param campaignKey - campaign key
   * @param variation   - variation instance
   */
  private void setVariation(String userId, String campaignKey, Variation variation, String goalIdentifier) {
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

      if (goalIdentifier != null) {
        variationMap.put(Storage.User.goalIdentifier, goalIdentifier);
      }

      try {
        this.userStorage.set(variationMap);
      } catch (Exception e) {
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("USER_STORAGE_SERVICE_SET_FAILED"), new HashMap<String, String>() {
          {
            put("userId", userId);
            put("error", e.getLocalizedMessage());
          }
        }), e.getStackTrace());
      }
    }
  }

  /**
   * Store variation info in user storage, if available.
   *
   * @param variation   - variation instance
   * @param campaignKey - campaign key
   * @param userId      - user id
   */
  private void setVariationInUserStorage(Variation variation, String campaignKey, String userId, String goalIdentifier) {
    // Set variation in user storage service if defined by the customer.
    if (variation != null) {
      String variationName = variation.getName();
      if (this.userStorage != null) {
        setVariation(userId, campaignKey, variation, goalIdentifier);
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("SETTING_DATA_USER_STORAGE_SERVICE"), new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", userId);
          }
        }));
      }
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("USER_VARIATION_STATUS"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
          put("status", "got Variation" +  variationName);
        }
      }));
    } else {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("USER_VARIATION_STATUS"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
          put("status", "got no Variation");
        }
      }));
    }
  }

  /**
   * set the goal in the user storage.
   *
   * @param userStorageMap - User storage Map
   * @param goalIdentifier - goalIdentifier String
   * @param userId         - userId string
   * @param campaign       - campaign object
   */
  public void setGoalInUserStorage(Map<String, String> userStorageMap, String goalIdentifier, String userId, Campaign campaign) {
    try {
      ArrayList<String> goalList = StorageUtils.stringToArray(userStorageMap.get(UserStorage.goalIdentifier));
      if (goalList.isEmpty() || !goalList.contains(goalIdentifier)) {
        goalList.add(goalIdentifier);
      }
      String goalString = StorageUtils.arrayToString(goalList);
      userStorageMap.put(UserStorage.goalIdentifier, goalString);
      userStorage.set(userStorageMap);
    } catch (Exception e) {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("SET_USER_STORAGE_SERVICE_FAILED"), new HashMap<String, String>() {
        {
          put("userId", userId);
        }
      }), e);
    }
  }

  /**
   * check if the goal is already tracked.
   *
   * @param goalIdentifier - goalIdentifier string
   * @param userStorageMap - UserStorageData object
   * @return true if goal is found in user storage, else false.
   */
  private boolean checkGoalTracked(String goalIdentifier, Map<String, String> userStorageMap) {
    ArrayList<String> goalList = StorageUtils.stringToArray(userStorageMap.get(UserStorage.goalIdentifier));
    return goalList != null && goalList.contains(goalIdentifier);
  }

  /**
   * check if multi conversion allowed.
   *
   * @param goalIdentifier - goalIdentifier string
   * @param campaign - campaign instance
   * @return true if multi conversion allowed, else false
   */
  private boolean checkMCA(String goalIdentifier, Campaign campaign) {
    Goal goal = TrackCampaign.getGoalId(campaign, goalIdentifier);
    boolean isMCA = (goal.getMCA() != null && goal.getMCA() == -1) && Objects.equals(goal.getType(), "REVENUE_TRACKING");
    return isMCA;
  }

  /**
   * check if goal has where condition.
   *
   * @param goalIdentifier - goalIdentifier string
   * @param campaign - campaign instance
   * @return true if goal contains hasProps and is equal to true, else false
   */
  private boolean checkHasProps(String goalIdentifier, Campaign campaign) {
    Goal goal = TrackCampaign.getGoalId(campaign, goalIdentifier);
    return goal.getHasProps();
  }


  /**
   * Check if the campaign is activated before.
   *
   * @param apiName  - name of the API
   * @param userId   - user id string
   * @param campaign - campaign instance
   * @return true if campaign is activated, else false
   */
  private boolean isCampaignActivated(String apiName, String userId, Campaign campaign) {
    if (!apiName.equalsIgnoreCase(APIEnums.API_TYPES.ACTIVATE.value())
            && !apiName.equalsIgnoreCase(APIEnums.API_TYPES.IS_FEATURE_ENABLED.value())) {

      LOGGER.warn(LoggerService.getComputedMsg(LoggerService.getInstance().warningMessages.get("CAMPAIGN_NOT_ACTIVATED"), new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("api", apiName);
        }
      }));
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("CAMPAIGN_NOT_ACTIVATED"), new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("reason", apiName.equalsIgnoreCase(APIEnums.API_TYPES.TRACK.value()) ? "track it" : "get the decision/value");
        }
      }));
      return false;
    }
    return true;
  }

  private void executeIntegrationsCallback(boolean fromUserStorage, Campaign campaign, Variation variation, boolean isUserWhitelisted) {
    if (variation != null) {
      integrationsMap.put("fromUserStorageService", fromUserStorage);
      integrationsMap.put("isUserWhitelisted", isUserWhitelisted);
      if (campaign.getType().equals(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        integrationsMap.put("isFeatureEnabled", true);
      } else {
        if (campaign.getType().equals(CampaignEnums.CAMPAIGN_TYPES.FEATURE_TEST.value())) {
          integrationsMap.put("isFeatureEnabled", variation.getIsFeatureEnabled());
        }
        integrationsMap.put("variationName", variation.getName());
        integrationsMap.put("variationId", variation.getId());
      }
      hooksManager.execute(integrationsMap);
    }
  }

  private void initIntegrationMap(Campaign campaign, String apiName, String userId, String goalIdentifier,
                                  Map<String, ?> customVariables, Map<String, ?> variationTargetingVariables) {
    integrationsMap.put("campaignId", campaign.getId());
    integrationsMap.put("campaignKey", campaign.getKey());
    integrationsMap.put("campaignType", campaign.getType());
    integrationsMap.put("customVariables", customVariables == null ? new HashMap<>() : customVariables);
    integrationsMap.put("event", HooksEnum.DECISION_TYPES.CAMPAIGN_DECISION.value());
    integrationsMap.put("goalIdentifier", goalIdentifier);
    integrationsMap.put("isForcedVariationEnabled", campaign.getIsForcedVariationEnabled());
    integrationsMap.put("sdkVersion", UriEnums.SDK_VERSION.toString());
    integrationsMap.put("source", apiName);
    integrationsMap.put("userId", userId);
    integrationsMap.put("variationTargetingVariables", variationTargetingVariables == null ? new HashMap<>() : variationTargetingVariables);
    integrationsMap.put("vwoUserId", UUIDUtils.getUUId(accountId, userId));
    if (campaign.getName() != null) {
      integrationsMap.put("campaignName", campaign.getName());
    }
  }
}
