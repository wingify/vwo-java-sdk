package com.vwo.bucketing;

import com.vwo.models.Campaign;
import com.vwo.models.Variation;
import com.vwo.userprofile.UserProfileService;
import com.vwo.userprofile.UserProfileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BucketingService {

    private final Bucketer bucketer;
    private final UserProfileService userProfileService;
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketingService.class);

    public BucketingService(Bucketer bucketer, UserProfileService userProfileService) {
        this.bucketer = bucketer;
        this.userProfileService = userProfileService;
    }

    public Variation getVariation(Campaign campaign, String userId) {
        if (!campaign.getStatus().equalsIgnoreCase("RUNNING")) {
            LOGGER.error("Campaign is Inactive. Unable to process.");
            return null;
        }

        Variation variation;

        //lookup in userProfileManager
        if (userProfileService != null) {
            try {
                //to do :
                Map<String, Object> userProfileMap = userProfileService.lookup(userId, campaign.getKey());
                if (userProfileMap == null) {
                    LOGGER.info("Not able to fetch data from UserProfileService");
                } else if (UserProfileUtils.isValidUserProfileMap(userProfileMap)) {
                    variation = getStoredVariation(userProfileMap, userId, campaign);

                    if (variation != null) {
                        LOGGER.info("Looked into UserProfileService for userId:{} successful",userId);
                        LOGGER.debug("Got stored variation for UserId:{} of Campaign:{} as Variation: {}, found in UserProfileService", userId, campaign, variation.getName());
                        return variation;
                    }else{
                        LOGGER.debug("No stored variation for UserId:{} of Campaign:{} found in UserProfileService",userId,variation.getName());
                    }
                } else {
                    LOGGER.warn("The UserProfileService returned an invalid map.");
                }
            } catch (Exception e) {
                LOGGER.warn("Data not found. Will proceed without UserProfileService.\n Variation might be different if configuration is changed");
            }
        }else {
            LOGGER.debug("UserProfileService is disabled.");
        }

        //get variation
        variation = bucketer.getBucket(campaign, userId);

        //save variation
        if (variation != null) {
            LOGGER.info("UserId:{} of Campaign:{} got variation: {}",userId,campaign.getKey(),variation.getName());
            if (userProfileService != null) {
                saveVariation(userId, campaign, variation);
                LOGGER.info("Saving into UserProfileService for userId:{} successful",userId);
                return variation;
            } else {
                LOGGER.debug("No UserProfileService to save data");
            }
        }

        LOGGER.info("UserId:{} of Campaign:{} did not get any variation",userId,campaign.getKey());
        return variation;

    }

    private Variation getStoredVariation(Map<String, Object> userProfileMap, String userId, Campaign campaign) {

        String userIdFromMap = (String) userProfileMap.get(UserProfileService.userId);
        if (!userIdFromMap.equalsIgnoreCase(userId)) {
            return null;
        }
        Map<String, Map<String, String>> campaignBucketMap = (Map<String, Map<String, String>>) userProfileMap.get(UserProfileService.campaignKey);
       boolean getCampaignName=campaignBucketMap.containsKey(campaign.getKey());
       if(!getCampaignName){
           LOGGER.info("Invalid Campaign key present");
           return null;
       }
        String variationName = getVariationKey(campaignBucketMap);
        if (variationName != null) {
            for (Variation variation : campaign.getVariations()) {
                if (variation.getName().equalsIgnoreCase(variationName)) {
                    return variation;
                }
            }
        }
        return null;
    }

    private String getVariationKey(Map<String, Map<String, String>> campaignBucketMap) {
        String variationName = null;
        for (Map.Entry<String, Map<String, String>> entry : campaignBucketMap.entrySet()) {
            variationName = entry.getValue().get(UserProfileService.variationKey);
        }
        return variationName;
    }


    void saveVariation(String userId, Campaign campaign, Variation variation) {
        // only save if the user has implemented a user profile service
        if (userProfileService != null) {
            String campaignId = campaign.getKey();
            String variationId = variation.getName();

            Map<String,Object> campaignKeyMap = new HashMap<>();
            Map<String, String> variationKeyMap = new HashMap<>();
            variationKeyMap.put(UserProfileService.variationKey, variationId);
            campaignKeyMap.put(campaignId,variationKeyMap);

            //set
            Map<String, Object> campaignBucketMap = new HashMap<>();
            campaignBucketMap.put(UserProfileService.userId, userId);
            campaignBucketMap.put(UserProfileService.campaignKey, variationKeyMap);

            //save variation
            try {
                userProfileService.save(campaignBucketMap);
            } catch (Exception e) {
                LOGGER.error("saving user profile failed.", e.getStackTrace());
            }
        }
    }
}
