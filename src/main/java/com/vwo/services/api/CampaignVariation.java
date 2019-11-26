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
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Variation;
import com.vwo.utils.ValidationUtils;

import java.util.HashMap;

public class CampaignVariation {
  private static final Logger LOGGER = Logger.getLogger(CampaignVariation.class);

  /**
   * Get variation name.
   *
   * @param campaignKey        Campaign key
   * @param userId             User ID
   * @param settingFile  Settings file Configuration
   * @param variationDecider   Variation decider service
   * @return Variation name
   */
  public static String getVariationName(String campaignKey, String userId, SettingFile settingFile, VariationDecider variationDecider) {
    LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.INITIATING_GET_VARIATION.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("campaignKey", campaignKey);
      }
    }));

    try {
      if (!ValidationUtils.isValidParams(
          new HashMap<String, Object>() {
            {
              put("campaignKey", campaignKey);
              put("userId", userId);
            }
          }
      )) {
        return null;
      }

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value());
        return null;
      } else if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_API.value(new HashMap<String, String>() {
          {
            put("api","getVariation");
            put("userId",userId);
            put("campaignKey",campaignKey);
            put("campaignType",campaign.getType());
          }
        }));
        return null;
      }

      return CampaignVariation.getCampaignVariationName(campaign, userId, variationDecider);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return null;
    }
  }

  public static String getCampaignVariationName(Campaign campaign, String userId, VariationDecider variationDecider) {
    Variation variation = variationDecider.getVariation(campaign, userId);
    return variation != null ? variation.getName() : null;
  }
}
