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

package com.vwo.services.api;

import com.vwo.enums.APIEnums;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.settings.SettingFile;
import com.vwo.enums.CampaignEnums;
import com.vwo.enums.GoalEnums;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpRequest;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Goal;
import com.vwo.models.Variation;
import com.vwo.utils.CampaignUtils;
import com.vwo.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackCampaign {
  private static final Logger LOGGER = Logger.getLogger(TrackCampaign.class);

  /**
   * Get variation, tracks conversion event and send to VWO server.
   *
   * @param campaignKey       Campaign key or array of Strings containing campaign keys or null
   * @param userId            User ID
   * @param goalIdentifier    Goal key
   * @param revenueValue      revenue generated on triggering the goal
   * @param settingFile Settings File Configuration
   * @param variationDecider  Variation decider service
   * @param isDevelopmentMode Development mode flag.
   * @param CustomVariables   Pre Segmentation custom variables
   * @param variationTargetingVariables  User Whitelisting Targeting variables
   * @return Map containing the campaign name and their boolean status representing if tracked or not, and null if something went wrong.
   */
  public static Map<String, Boolean> trackGoal(
      Object campaignKey,
      String userId,
      String goalIdentifier,
      Object revenueValue,
      SettingFile settingFile,
      VariationDecider variationDecider,
      boolean isDevelopmentMode,
      Map<String, ?> CustomVariables,
      Map<String, ?> variationTargetingVariables,
      GoalEnums.GOAL_TYPES goalsToTrack
  ) {
    try {
      if (!TrackCampaign.isTrackParamsValid(campaignKey, userId, goalIdentifier)) {
        return null;
      }

      ArrayList<Campaign> campaignList = new ArrayList<>();

      if (campaignKey == null) {
        campaignList = CampaignUtils.getCampaignFromGoalIdentifier(goalIdentifier, settingFile.getSettings().getCampaigns());
      } else {
        campaignKey = campaignKey instanceof String ? new String[] { (String) campaignKey } : campaignKey;
        for (String campaign : (String[]) campaignKey) {
          campaignList.add(settingFile.getCampaign(campaign));
        }
      }

      if (campaignList.size() == 0) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.NO_CAMPAIGN_FOUND.value(new HashMap<String, String>() {
          {
            put("goalIdentifier", goalIdentifier);
          }
        }));
        return null;
      }

      Map<String, Boolean> trackStatus = new HashMap<>();
      for (int i = 0; i < campaignList.size(); i++) {
        Campaign campaign = campaignList.get(i);
        String key = campaign == null ? ((String[])campaignKey)[0] : campaign.getKey();
        trackStatus.put(key, false);

        if (campaign == null) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value(new HashMap<String, String>() {
            {
              put("campaignKey", key);
            }
          }));
        } else if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_API.value(new HashMap<String, String>() {
            {
              put("api", "track");
              put("userId", userId);
              put("campaignKey", key);
              put("campaignType", campaign.getType());
            }
          }));
        } else {
          Goal goal = TrackCampaign.getGoalId(campaign, goalIdentifier);

          if (goal == null) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TRACK_API_GOAL_NOT_FOUND.value(new HashMap<String, String>() {
              {
                put("goalIdentifier", goalIdentifier);
                put("userId", userId);
                put("campaignKey", key);
              }
            }));
          } else if (goal.getType().equalsIgnoreCase(GoalEnums.GOAL_TYPES.REVENUE.value()) && revenueValue == null) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_GOAL_REVENUE.value(new HashMap<String, String>() {
              {
                put("goalIdentifier", goalIdentifier);
                put("userId", userId);
                put("campaignKey", key);
              }
            }));
          } else if (goalsToTrack.value().equals(GoalEnums.GOAL_TYPES.ALL.value()) || goalsToTrack.value().equals(goal.getType())) {
            Object revenue = goal.getType().equalsIgnoreCase(GoalEnums.GOAL_TYPES.CUSTOM.value()) ? null : revenueValue;

            String variation = CampaignVariation.getCampaignVariationName(campaign, userId, variationDecider, CustomVariables, variationTargetingVariables);

            if (variation != null) {
              TrackCampaign.sendTrackCall(
                  settingFile,
                  campaign,
                  userId,
                  goal,
                  CampaignUtils.getVariationObjectFromCampaign(campaign, variation),
                  revenue,
                  isDevelopmentMode
              );

              trackStatus.put(key, true);
            }
          }
        }
      }

      return trackStatus;
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return null;
    }
  }

  private static boolean isTrackParamsValid(Object campaignKey, String userId, String goalIdentifier) {
    return ValidationUtils.isValidParams(
        new HashMap<String, Object>() {
          {
            put("campaignKey", campaignKey);
            put("userId", userId);
            put("goalIdentifier", goalIdentifier);
          }
        },
        APIEnums.API_TYPES.TRACK
    );
  }

  private static Goal getGoalId(Campaign campaign, String goalIdentifier) {
    for (Goal singleGoal : campaign.getGoals()) {
      if (goalIdentifier.equalsIgnoreCase(singleGoal.getIdentifier())) {
        return singleGoal;
      }
    }
    return null;
  }

  private static void sendTrackCall(
      SettingFile settingFile,
      Campaign campaign,
      String userId,
      Goal goal,
      Variation variation,
      Object revenueValue,
      boolean isDevelopmentMode
  ) {
    HttpParams httpParams = HttpRequestBuilder.getGoalParams(settingFile, campaign, userId, goal, variation, revenueValue);
    try {
      if (!isDevelopmentMode) {
        HttpRequest.send(httpParams);
      }
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
    }
  }
}
