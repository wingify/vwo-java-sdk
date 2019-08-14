package com.vwo.bucketing;

import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.logger.LoggerManager;
import com.vwo.models.Campaign;
import com.vwo.models.Variation;
import com.vwo.userprofile.UserProfileService;
import com.vwo.userprofile.UserProfileUtils;

import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;

public class BucketingService {

  private final Bucketer bucketer;
  private final UserProfileService userProfileService;
  private static final LoggerManager LOGGER = LoggerManager.getLogger(BucketingService.class);

  public BucketingService(Bucketer bucketer, UserProfileService userProfileService) {
    this.bucketer = bucketer;
    this.userProfileService = userProfileService;
  }

  /**
   * Determines the variation of a user for a campaign.
   *
   * @param campaign - campaign instance
   * @param userId - user id string
   * @return
   */
  public Variation getVariation(Campaign campaign, String userId) {
    if (!campaign.getStatus().equalsIgnoreCase("RUNNING")) {
      LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CAMPAIGN_NOT_RUNNING.value(new Pair<>("campaignTestKey", campaign.getKey())));
      return null;
    }

    Variation variation;

    // Try to lookup in user profile service to get variation.
    if (this.userProfileService != null) {
      try {
        Map<String, String> userProfileMap = this.userProfileService.lookup(userId, campaign.getKey());

        if (userProfileMap == null) {
          LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.NO_DATA_USER_PROFILE_SERVICE.value());
        } else if (UserProfileUtils.isValidUserProfileMap(userProfileMap)) {
          variation = getStoredVariation(userProfileMap, userId, campaign);

          if (variation != null) {
            LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.GOT_STORED_VARIATION.value(
                    new Pair<>("variationName", variation.getName()),
                    new Pair<>("campaignTestKey", campaign.getKey()),
                    new Pair<>("userId", userId)
            ));
            return variation;
          } else {
            LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.NO_STORED_VARIATION.value(new Pair<>("campaignTestKey", campaign.getKey()), new Pair<>("userId", userId)));
          }
        } else {
          LOGGER.warn(LoggerMessagesEnum.WARNING_MESSAGES.INVALID_USER_PROFILE_MAP.value(new Pair<>("map", userProfileMap.toString())));
        }
      } catch (Exception e) {
        LOGGER.warn(LoggerMessagesEnum.WARNING_MESSAGES.NO_DATA_IN_USER_PROFILE.value());
      }
    } else {
      LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.NO_USER_PROFILE_DEFINED.value());
    }

    // Get variation using campaign settings for a user.
    variation = bucketer.bucketUserToVariation(campaign, userId);

    // Save variation in user profile service if defined by the customer.
    if (variation != null) {
      if (this.userProfileService != null) {
        saveVariation(userId, campaign, variation);
        LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.SAVED_IN_USER_PROFILE_SERVICE.value(new Pair<>("userId", userId), new Pair<>("variation", variation.getName())));
      }
      LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.GOT_VARIATION_FOR_USER.value(
              new Pair<>("userId", userId),
              new Pair<>("campaignTestKey", campaign.getKey()),
              new Pair<>("variation", variation.getName())
      ));
    } else {
      LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.USER_NOT_PART_OF_CAMPAIGN.value(new Pair<>("userId", userId), new Pair<>("campaignTestKey", campaign.getKey())));
    }

    return variation;
  }

  /**
   * Fetch the variation info of a user for a campaign from user defined user profile service.
   *
   * @param userProfileMap - User profile service hash map
   * @param userId - user ID
   * @param campaign - campaign instance
   * @return
   */
  private Variation getStoredVariation(Map<String, String> userProfileMap, String userId, Campaign campaign) {

    String userIdFromMap = userProfileMap.get(UserProfileService.userId);
    String campaignKey = userProfileMap.get(UserProfileService.campaignKey);
    String variationName = userProfileMap.get(UserProfileService.variationKey);

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
   * Save variation info if user profile service is provided the the user.
   *
   * @param userId - user id
   * @param campaign - campaign instance
   * @param variation - variation instance
   */
  private void saveVariation(String userId, Campaign campaign, Variation variation) {
    if (this.userProfileService != null) {
      String campaignId = campaign.getKey();
      String variationId = variation.getName();

      Map<String, String> campaignBucketMap = new HashMap<>();

      //set
      campaignBucketMap.put(UserProfileService.userId, userId);
      campaignBucketMap.put(UserProfileService.campaignKey, campaignId);
      campaignBucketMap.put(UserProfileService.variationKey, variationId);

      try {
        this.userProfileService.save(campaignBucketMap);
      } catch (Exception e) {
        LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.SAVE_USER_PROFILE_SERVICE_FAILED.value(new Pair<>("userId", userId)), e.getStackTrace());
      }
    }
  }
}