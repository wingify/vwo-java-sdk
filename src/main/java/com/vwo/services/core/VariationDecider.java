/**
 * Copyright 2019-2021 Wingify Software Pvt. Ltd.
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
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.enums.SegmentationTypeEnums;
import com.vwo.enums.StatusEnums;
import com.vwo.enums.HooksEnum;
import com.vwo.enums.UriEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Settings;
import com.vwo.models.Variation;
import com.vwo.services.http.HttpRequestBuilder;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VariationDecider {

  private final BucketingService bucketingService;
  private final Storage.User userStorage;
  private final boolean shouldTrackReturningUser;
  public boolean isStoredVariation;
  private final HooksManager hooksManager;
  private final int accountId;
  private Map<String, Object> integrationsMap;
  private static final Logger LOGGER = Logger.getLogger(VariationDecider.class);

  public VariationDecider(BucketingService bucketingService, Storage.User userStorage, boolean shouldTrackReturningUser,
                          HooksManager hooksManager, int accountId) {
    this.bucketingService = bucketingService;
    this.userStorage = userStorage;
    this.shouldTrackReturningUser = shouldTrackReturningUser;
    this.hooksManager = hooksManager;
    this.accountId = accountId;
  }


  public boolean getShouldTrackReturningUser() {
    return shouldTrackReturningUser;
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
   * @param shouldTrackReturningUser       Boolean value to check if the goal should be tracked again or not.
   * @return variation name or null if not found.
   */
  public Variation getVariation(
          Settings settings,
          String apiName,
          Campaign campaign,
          String userId,
          Map<String, ?> rawCustomVariables,
          Map<String, ?> rawVariationTargetingVariables,
          String goalIdentifier,
          Boolean shouldTrackReturningUser
  ) {
    // Default initialization(s)
    isStoredVariation = false;
    integrationsMap = new HashMap<String, Object>();
    initIntegrationMap(campaign, apiName, userId, goalIdentifier, rawCustomVariables, rawVariationTargetingVariables);
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

    Map<String, Object> groupDetails = CampaignUtils.isPartOfGroup(settings, campaign.getId());
    if (!groupDetails.isEmpty()) {
      integrationsMap.put("groupId", groupDetails.get("groupId"));
      integrationsMap.put("groupName", groupDetails.get("groupName"));
    }


    Variation whitelistedVariation = checkForWhitelisting(campaign, userId, variationTargetingVariables, false);
    if (whitelistedVariation != null) {
      return whitelistedVariation;
    }

    Variation userVariation = checkForUserStorage(apiName, campaign, userId, goalIdentifier, shouldTrackReturningUser, false);
    if (userVariation == null || userVariation.getId() != -1) {
      return userVariation;
    }

    // Check if user satisfies pre segmentation. If not, return null.
    Boolean isPreSegmentationValid = checkForPreSegmentation(campaign, userId, customVariables, false);
    if (!(isPreSegmentationValid && BucketingService.getUserHashForCampaign(userId, campaign.getPercentTraffic(), true) != -1)) {
      return null;
    }

    if (groupDetails.containsKey("groupId")) {
      List<Campaign> campaignList = CampaignUtils.getGroupCampaigns(settings, (int) groupDetails.get("groupId"));

      if (campaignList.isEmpty()) {
        return null;
      }

      if (checkForStorageAndWhitelisting(apiName, campaignList, campaign, userId, goalIdentifier, shouldTrackReturningUser, variationTargetingVariables)) {
        return null;
      }

      Map<String, List<Campaign>> processedCampaigns = getEligibleCampaigns(campaignList, userId, customVariables);

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
      LOGGER.info(LoggerMessagesEnums.DEBUG_MESSAGES.GOT_ELIGIBLE_CAMPAIGNS.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("groupName", (String) groupDetails.get("groupName"));
          put("eligibleCampaignsKey", finalEligibleCampaignKeys);
          put("nonEligibleCampaignsKey", finalInEligibleCampaignKeys);
        }
      }));

      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.GOT_ELIGIBLE_CAMPAIGNS.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("noOfEligibleCampaigns", String.valueOf(processedCampaigns.get("eligibleCampaigns").size()));
          put("noOfGroupCampaigns", String.valueOf(campaignList.size()));
          put("groupName", (String) groupDetails.get("groupName"));
        }
      }));

      if (processedCampaigns.get("eligibleCampaigns").size() == 1) {
        return evaluateTrafficAndGetVariation(processedCampaigns.get("eligibleCampaigns").get(0), userId, goalIdentifier);
      } else {
        return normalizeAndFindWinningCampaign(processedCampaigns.get("eligibleCampaigns"), campaign, userId, goalIdentifier, (String) groupDetails.get("groupName"));
      }
    } else {
      return evaluateTrafficAndGetVariation(campaign, userId, goalIdentifier);
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
  private Variation checkForWhitelisting(Campaign campaign, String userId, Map<String, ?> variationTargetingVariables, boolean disableLogs) {
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
          }), disableLogs);
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
          }), disableLogs);
        }
      });

      if (whiteListedVariations.size() != 0) {
        Variation whiteListedVariation = whiteListedVariations.get(0);

        if (whiteListedVariations.size() > 1) {
          CampaignUtils.rationalizeVariationsWeights(whiteListedVariations);
          SettingsFileUtil.setVariationRange(whiteListedVariations);
          whiteListedVariation = (Variation) bucketingService.getUserVariation(whiteListedVariations, campaign.getKey(), 100, userId);
        }

        // this.setVariationInUserStorage(whiteListedVariation, campaign.getKey(), userId);

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
        }), disableLogs);
        if (!disableLogs) {
          executeIntegrationsCallback(false, campaign, whiteListedVariation, true);
        }
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
        }), disableLogs);
      }
      return null;
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.WHITELISTING_SKIPPED.value(new HashMap<String, String>() {
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
   * @param shouldTrackReturningUser - Boolean value to check if the goal should be tracked again or not
   * @return Stored variation.
   */
  private Variation checkForUserStorage(String apiName, Campaign campaign, String userId, String goalIdentifier, Boolean shouldTrackReturningUser, boolean disableLogs) {
    Variation variation = new Variation();
    variation.setId(-1);
    // Try to lookup in user storage service to get variation.
    if (this.userStorage != null) {
      try {
        Map<String, String> userStorageMap = this.userStorage.get(userId, campaign.getKey());

        if (userStorageMap == null) {
          if (!isCampaignActivated(apiName, userId, campaign)) {
            return null;
          } else {
            LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.NO_DATA_USER_STORAGE_SERVICE.value(), disableLogs);
          }
        } else if (StorageUtils.isValidUserStorageMap(userStorageMap)) {
          if (shouldTrackReturningUser == null) {
            shouldTrackReturningUser = this.shouldTrackReturningUser;
          }

          if (goalIdentifier != null && !shouldTrackReturningUser) {
            if (checkGoalTracked(goalIdentifier, userStorageMap)) {
              LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.GOAL_ALREADY_TRACKED.value(new HashMap<String, String>() {
                {
                  put("goalIdentifer", goalIdentifier);
                  put("campaignKey", campaign.getKey());
                  put("userId", userId);
                }
              }), disableLogs);
              return null;
            }
          } else if (goalIdentifier != null && shouldTrackReturningUser) {
            LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.GOAL_SHOULD_TRACK_AGAIN.value(new HashMap<String, String>() {
              {
                put("goalIdentifer", goalIdentifier);
                put("campaignKey", campaign.getKey());
                put("userId", userId);
              }
            }), disableLogs);
          }

          variation = getStoredVariation(userStorageMap, userId, campaign);

          if (variation != null) {
            String variationName = variation.getName();
            LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.GOT_STORED_VARIATION.value(new HashMap<String, String>() {
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
              LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.NO_STORED_VARIATION.value(new HashMap<String, String>() {
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
            LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.INVALID_USER_STORAGE_MAP.value(new HashMap<String, String>() {
              {
                put("map", userStorageMap.toString());
              }
            }), disableLogs);
          }
        }
      } catch (Exception e) {
        LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.NO_DATA_IN_USER_STORAGE.value(), disableLogs);
      }
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.NO_USER_STORAGE_DEFINED.value(), disableLogs);
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
      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.SEGMENTATION_STATUS.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaign.getKey());
          put("customVariables", customVariables.toString());
          put("segmentationType", SegmentationTypeEnums.PRE_SEGMENTATION.value());
          put("variation", "(No variation i.e. Presegment)");
          put("status", isPresegmentValid ? StatusEnums.PASSED.value() : StatusEnums.FAILED.value());
        }
      }), disableLogs);

      return isPresegmentValid;
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SEGMENTATION_SKIPPED.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("variation", "(No variation i.e. Presegment)");
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
   * @param shouldTrackReturningUser    - Boolean value to check if the goal should be tracked again or not
   * @param variationTargetingVariables - User Whitelisting Targeting variables
   * @return true, if whitelisting/storage is satisfied for any campaign.
   */
  private boolean checkForStorageAndWhitelisting(String apiName, List<Campaign> campaignList, Campaign calledCampaign, String userId, String goalIdentifier,
                                                 Boolean shouldTrackReturningUser, Map<String, ?> variationTargetingVariables) {
    boolean otherCampaignWinner = false;
    for (Campaign campaign : campaignList) {
      if (campaign.getId().equals(calledCampaign.getId())) {
        continue;
      }
      Variation whitelistedVariation = checkForWhitelisting(campaign, userId, variationTargetingVariables, true);
      if (whitelistedVariation != null) {
        otherCampaignWinner = true;
        break;
      }

      Variation storedVariation = checkForUserStorage(apiName, campaign, userId, goalIdentifier, shouldTrackReturningUser, true);
      if (storedVariation != null && storedVariation.getId() != -1) {
        otherCampaignWinner = true;
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
  private Map<String, List<Campaign>> getEligibleCampaigns(List<Campaign> campaignList, String userId, Map<String, ?> customVariables) {
    List<Campaign> eligibleCampaigns = new ArrayList<>();
    List<Campaign> inEligibleCampaigns = new ArrayList<>();
    for (Campaign campaign : campaignList) {
      if (checkForPreSegmentation(campaign, userId, customVariables, true) && BucketingService.getUserHashForCampaign(userId, campaign.getPercentTraffic(), true) != -1) {
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
  private Variation evaluateTrafficAndGetVariation(Campaign campaign, String userId, String goalIdentifier) {
    // Get variation using campaign settings for a user.
    Variation variation = null;
    variation = (Variation) bucketingService.getUserVariation(campaign.getVariations(), campaign.getKey(), campaign.getPercentTraffic(), userId);
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
  private Variation normalizeAndFindWinningCampaign(List<Campaign> shortlistedCampaigns, Campaign calledCampaign, String userId, String goalIdentifier, String groupName) {

    for (Campaign campaign : shortlistedCampaigns) {
      campaign.setWeight((double) (100 / shortlistedCampaigns.size()));
    }
    SettingsFileUtil.setCampaignRange(shortlistedCampaigns);
    Long bucketHash = BucketingService.getUserHashForCampaign(userId, 100, true);
    int variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
    Campaign winnerCampaign = (Campaign) bucketingService.getAllocatedItem(shortlistedCampaigns, variationHashValue);

    LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.GOT_WINNER_CAMPAIGN.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("campaignKey", winnerCampaign.getKey());
        put("groupName", groupName);
      }
    }));

    if (winnerCampaign.getId().equals(calledCampaign.getId())) {
      return evaluateTrafficAndGetVariation(winnerCampaign, userId, goalIdentifier);
    } else {
      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.CALLED_CAMPAIGN_NOT_WINNER.value(new HashMap<String, String>() {
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
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.SAVE_USER_STORAGE_SERVICE_FAILED.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaign.getKey());
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
   * Check if the campaign is activated before.
   *
   * @param apiName  - name of the API
   * @param userId   - user id string
   * @param campaign - campaign instance
   * @return true if campaign is activated, else false
   */
  private boolean isCampaignActivated(String apiName, String userId, Campaign campaign) {
    if (!apiName.equalsIgnoreCase(APIEnums.API_TYPES.ACTIVATE.value())
            && !apiName.equalsIgnoreCase(APIEnums.API_TYPES.IS_FEATURE_ENABLED.value())
            && !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {

      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.CAMPAIGN_NOT_ACTIVATED.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("api", apiName);
        }
      }));
      LOGGER.debug(LoggerMessagesEnums.INFO_MESSAGES.CAMPAIGN_NOT_ACTIVATED.value(new HashMap<String, String>() {
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
