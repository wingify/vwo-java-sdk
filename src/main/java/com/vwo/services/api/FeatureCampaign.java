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

package com.vwo.services.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.APIEnums;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.services.batch.BatchEventQueue;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.settings.SettingFile;
import com.vwo.enums.CampaignEnums;
import com.vwo.enums.FeatureEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Variable;
import com.vwo.models.Variation;
import com.vwo.utils.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureCampaign {
  private static final Logger LOGGER = Logger.getLogger(FeatureCampaign.class);

  /**
   * Identifies whether the user became part of feature rollout/test or not.
   *
   * @param campaignKey       Unique campaign test key
   * @param userId            ID assigned to a user
   * @param settingFile Settings File Configuration
   * @param variationDecider  Variation decider service
   * @param isDevelopmentMode Development mode flag.
   * @param batchEventQueue   Event Batching Queue.
   * @param usageStats        usage info collected at the time of VWO instantiation.
   * @param CustomVariables    Pre Segmentation custom variables
   * @param variationTargetingVariables    User Whitelisting Targeting variables
   * @param shouldTrackReturningUser    Boolean value to check if the goal should be tracked again or not.
   * @return Boolean corresponding to whether user became part of feature.
   */
  public static boolean isFeatureEnabled(
      String campaignKey,
      String userId,
      SettingFile settingFile,
      VariationDecider variationDecider,
      boolean isDevelopmentMode,
      BatchEventQueue batchEventQueue,
      Map<String, Integer> usageStats,
      Map<String, ?> CustomVariables,
      Map<String, ?> variationTargetingVariables,
      Boolean shouldTrackReturningUser
  ) {
    try {
      if (!ValidationUtils.isValidParams(
          new HashMap<String, Object>() {
            {
              put("campaignKey", campaignKey);
              put("userId", userId);
            }
          },
          APIEnums.API_TYPES.IS_FEATURE_ENABLED
      )) {
        return false;
      }

      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.INITIATING_IS_FEATURE_ENABLED.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
        }
      }));

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value(new HashMap<String, String>() {
          {
            put("campaignKey",campaignKey);
          }
        }));
        return false;
      } else if (
          !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())
          && !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_TEST.value())
      ) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_API.value(new HashMap<String, String>() {
          {
            put("api","isFeatureEnabled");
            put("userId",userId);
            put("campaignKey",campaignKey);
            put("campaignType",campaign.getType());
          }
        }));

        return false;
      }

      String variation = ActivateCampaign.activateCampaign(APIEnums.API_TYPES.IS_FEATURE_ENABLED.value(), campaign, userId, settingFile, variationDecider, isDevelopmentMode, batchEventQueue,
              CustomVariables, variationTargetingVariables, shouldTrackReturningUser, usageStats);

      if (variation == null) {
        return false;
      }

      if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        return true;
      } else {
        Variation variationDetails = campaign.getVariations().stream().filter(variationObj -> variationObj.getName().equalsIgnoreCase(variation)).findFirst().get();
        return variationDetails.getIsFeatureEnabled();
      }

    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return false;
    }
  }

  /**
   * Gets the feature variable of type string corresponding to the variable_key passed.
   *
   * @param campaignKey Unique campaign test key
   * @param userId ID assigned to a user
   * @param variableKey Variable name/key
   * @param variableType Expected Variable type
   * @param settingFile Settings File Configuration
   * @param variationDecider Variation decider service
   * @param CustomVariables Pre Segmentation custom variables
   * @param variationTargetingVariables User Whitelisting Targeting variables
   *
   * @return If variation is assigned then string variable corresponding to variation assigned otherwise null
   */
  public static Object getFeatureVariable(
      String campaignKey,
      String userId,
      String variableKey,
      String variableType,
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
              put("variableKey", variableKey);
            }
          },
          APIEnums.API_TYPES.GET_FEATURE_VARIABLE_VALUE
      )) {
        return null;
      }

      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.INITIATING_GET_FEATURE_VARIATION.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
          put("variableKey", variableKey);
        }
      }));

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value(new HashMap<String, String>() {
          {
            put("campaignKey",campaignKey);
          }
        }));
        return null;
      } else if (
              !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())
                      && !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_TEST.value())
      ) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_API.value(new HashMap<String, String>() {
          {
            put("api", "isFeatureEnabled");
            put("userId", userId);
            put("campaignKey", campaignKey);
            put("campaignType", campaign.getType());
          }
        }));
        return null;
      }

      String variation = CampaignVariation.getCampaignVariationName(APIEnums.API_TYPES.GET_FEATURE_VARIABLE_VALUE.value(), campaign, userId, variationDecider,
              CustomVariables, variationTargetingVariables);

      if (variation == null) {
        return null;
      }

      List<Variable> variables;

      if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        variables = campaign.getVariables();
      } else {

        Variation variationDetails = campaign.getVariations().stream().filter(variationObj -> variationObj.getName().equalsIgnoreCase(variation)).findFirst().get();

        if (!variationDetails.getIsFeatureEnabled()) {
          LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.FEATURE_NOT_ENABLED.value(new HashMap<String, String>() {
            {
              put("variation", "variation");
            }
          }));
          variationDetails = campaign.getVariations().stream().filter(variationObj -> variationObj.getId() == 1).findFirst().get();
        }

        variables = variationDetails.getVariables();
      }

      Variable variable = variables.stream().filter(variableObj -> variableObj.getKey().equalsIgnoreCase(variableKey)).findFirst().orElse(null);

      if (variable == null) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.VARIABLE_NOT_FOUND.value(new HashMap<String, String>() {
          {
            put("variableKey", variableKey);
            put("campaignKey", campaignKey);
            put("campaignType", campaign.getType());
            put("userId", userId);
          }
        }));
        return null;
      }

      if (variableType == null) {
        variableType = variable.getType().toLowerCase();
      } else if (!variable.getType().equalsIgnoreCase(variableType)) {
        String expectedVariableType = variableType;
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.VARIABLE_REQUESTED_WITH_WRONG_TYPE.value(new HashMap<String, String>() {
          {
            put("variableType", variable.getType());
            put("expectedVariableType", expectedVariableType);
          }
        }));
        return null;
      }

      LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.FEATURE_VARIABLE_FOUND.value(new HashMap<String, String>() {
        {
          put("variableKey", variableKey);
          put("campaignKey", campaignKey);
          put("variableValue", variable.getValue().toString());
          put("userId", userId);
        }
      }));

      return FeatureCampaign.getTypeCastedValue(variable.getValue(), variableType);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return null;
    }
  }

  private static Object getTypeCastedValue(Object value, String type) {
    if (type.equalsIgnoreCase(FeatureEnums.FEATURE_VARIABLE_TYPES.STRING.value())) {
      return value.toString();
    } else if (type.equalsIgnoreCase(FeatureEnums.FEATURE_VARIABLE_TYPES.INTEGER.value())) {
      try {
        return Integer.valueOf(value.toString());
      } catch (NumberFormatException e) {
        return ((Double) value).intValue();
      }
    } else if (type.equalsIgnoreCase(FeatureEnums.FEATURE_VARIABLE_TYPES.DOUBLE.value())) {
      return Double.valueOf(value.toString());
    } else if (type.equalsIgnoreCase(FeatureEnums.FEATURE_VARIABLE_TYPES.BOOLEAN.value())) {
      return Boolean.valueOf(value.toString());
    } else if (type.equalsIgnoreCase(FeatureEnums.FEATURE_VARIABLE_TYPES.JSON.value())) {
      try {
        if (ValidationUtils.isValidJSON(new ObjectMapper().writeValueAsString(value))) {
          return value;
        } else {
          return null;
        }
      } catch (JsonProcessingException e) {
        return null;
      }
    } else {
      return value;
    }
  }
}
