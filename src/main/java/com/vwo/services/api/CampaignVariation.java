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

package com.vwo.services.api;

import com.vwo.enums.APIEnums;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.models.response.Settings;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.settings.SettingFile;
import com.vwo.enums.CampaignEnums;
import com.vwo.logger.Logger;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Variation;
import com.vwo.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class CampaignVariation {
  private static final Logger LOGGER = Logger.getLogger(CampaignVariation.class);

  /**
   * Get variation name.
   *
   * @param campaignKey                 Campaign key
   * @param userId                      User ID
   * @param settingFile                 Settings file Configuration
   * @param variationDecider            Variation decider service
   * @param CustomVariables             Pre Segmentation custom variables
   * @param variationTargetingVariables User Whitelisting Targeting variables
   * @return Variation name
   */
  public static String getVariationName(
          String campaignKey,
          String userId,
          SettingFile settingFile,
          VariationDecider variationDecider,
          Map<String, ?> CustomVariables,
          Map<String, ?> variationTargetingVariables
  ) {
    try {
      if (!ValidationUtils.isValidParams(
              new HashMap<String, Object>() {
                {
                  put("campaignKey", campaignKey);
                  put("userId", userId);
                }
              },
              APIEnums.API_TYPES.GET_VARIATION_NAME
      )) {
        return null;
      }

      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.INITIATING_GET_VARIATION.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
        }
      }));

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value(new HashMap<String, String>() {
          {
            put("campaignKey", campaignKey);
          }
        }));
        return null;
      } else if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_API.value(new HashMap<String, String>() {
          {
            put("api", "getVariation");
            put("userId", userId);
            put("campaignKey", campaignKey);
            put("campaignType", campaign.getType());
          }
        }));
        return null;
      }

      return CampaignVariation.getCampaignVariationName(settingFile.getSettings(), APIEnums.API_TYPES.GET_VARIATION_NAME.value(), campaign, userId,
              variationDecider, CustomVariables, variationTargetingVariables);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return null;
    }
  }

  public static String getCampaignVariationName(
          Settings settings,
          String apiName,
          Campaign campaign,
          String userId,
          VariationDecider variationDecider,
          Map<String, ?> CustomVariables,
          Map<String, ?> variationTargetingVariables) {

    Variation variation = variationDecider.getVariation(settings, apiName, campaign, userId, CustomVariables,
            variationTargetingVariables, null);
    return variation != null ? variation.getName() : null;
  }

  public static String getCampaignVariationName(
          Settings settings,
          String apiName,
          Campaign campaign,
          String userId,
          VariationDecider variationDecider,
          Map<String, ?> CustomVariables,
          Map<String, ?> variationTargetingVariables,
          String goalIdentifier
  ) {
    Variation variation = variationDecider.getVariation(settings, apiName, campaign, userId, CustomVariables, variationTargetingVariables, goalIdentifier);
    return variation != null ? variation.getName() : null;
  }
}
