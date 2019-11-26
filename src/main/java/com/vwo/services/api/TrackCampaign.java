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

package com.vwo.services.api;

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

import java.util.HashMap;

public class TrackCampaign {
  private static final Logger LOGGER = Logger.getLogger(TrackCampaign.class);

  /**
   * Get variation, tracks conversion event and send to VWO server.
   *
   * @param campaignKey       Campaign key
   * @param userId            User ID
   * @param goalIdentifier    Goal key
   * @param revenueValue      revenue generated on triggering the goal
   * @param settingFile Settings File Configuration
   * @param variationDecider  Variation decider service
   * @param isDevelopmentMode Development mode flag.
   * @return Boolean value whether user is tracked or not.
   */
  public static boolean trackGoal(
      String campaignKey,
      String userId,
      String goalIdentifier,
      Object revenueValue,
      SettingFile settingFile,
      VariationDecider variationDecider,
      boolean isDevelopmentMode
  ) {
    try {
      if (!TrackCampaign.isTrackParamsValid(campaignKey, userId, goalIdentifier, settingFile)) {
        return false;
      }

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value());
        return false;
      } else if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_API.value(new HashMap<String, String>() {
          {
            put("api","trackGoal");
            put("userId",userId);
            put("campaignKey",campaignKey);
            put("campaignType",campaign.getType());
          }
        }));

        return false;
      }

      String variation = CampaignVariation.getCampaignVariationName(campaign, userId, variationDecider);

      if (variation != null) {
        Goal goal = TrackCampaign.getGoalId(campaign, goalIdentifier);

        if (goal == null) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TRACK_API_GOAL_NOT_FOUND.value(new HashMap<String, String>() {
            {
              put("goalIdentifier", goalIdentifier);
              put("userId", userId);
              put("campaignKey", campaign.getKey());
            }
          }));
          return false;
        } else if (goal.getType().equalsIgnoreCase(GoalEnums.GOAL_TYPES.REVENUE.value()) && revenueValue == null) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_GOAL_REVENUE.value(new HashMap<String, String>() {
            {
              put("goalIdentifier", goalIdentifier);
              put("userId", userId);
              put("campaignKey", campaign.getKey());
            }
          }));
          return false;
        } else if (goal.getType().equalsIgnoreCase(GoalEnums.GOAL_TYPES.CUSTOM.value())) {
          revenueValue = null;
        }

        TrackCampaign.sendTrackCall(
                settingFile,
            campaign,
            userId,
            goal,
            CampaignUtils.getVariationObjectFromCampaign(campaign, variation),
            revenueValue,
            isDevelopmentMode
        );

        return true;
      } else {
        LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.TRACK_API_VARIATION_NOT_FOUND.value(new HashMap<String, String>() {
          {
            put("userId", userId);
            put("campaignKey", campaignKey);
          }
        }));
      }

      return false;
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return false;
    }
  }

  private static boolean isTrackParamsValid(String campaignKey, String userId, String goalIdentifier, SettingFile settingFile) {
    return ValidationUtils.isValidParams(
        new HashMap<String, Object>() {
          {
            put("campaignKey", campaignKey);
            put("userId", userId);
            put("goalIdentifier", goalIdentifier);
          }
        }
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
