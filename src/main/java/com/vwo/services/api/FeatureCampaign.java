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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.APIEnums;
import com.vwo.logger.LoggerService;
import com.vwo.services.batch.BatchEventQueue;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.settings.SettingFile;
import com.vwo.services.storage.Storage;
import com.vwo.enums.CampaignEnums;
import com.vwo.enums.FeatureEnums;
import com.vwo.logger.Logger;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Variable;
import com.vwo.models.response.Variation;
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
   * @param CustomVariables   Pre Segmentation custom variables
   * @param variationTargetingVariables    User Whitelisting Targeting variables
   * @param clientUserAgent  User Agent of the visitor
   * @param userIPAddress     IP of the visitor
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
      Storage.User userStorage,
      String clientUserAgent,
      String userIPAddress

  ) {
    return isFeatureEnabledImpl(campaignKey, userId, settingFile, variationDecider,
      isDevelopmentMode, batchEventQueue, usageStats, CustomVariables, variationTargetingVariables,
      userStorage, clientUserAgent, userIPAddress);
  }

  // implement isFeatureEnabled
  public static boolean isFeatureEnabledImpl(
      String campaignKey,
      String userId,
      SettingFile settingFile,
      VariationDecider variationDecider,
      boolean isDevelopmentMode,
      BatchEventQueue batchEventQueue,
      Map<String, Integer> usageStats,
      Map<String, ?> CustomVariables,
      Map<String, ?> variationTargetingVariables,
      Storage.User userStorage,
      String clientUserAgent,
      String userIPAddress
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

      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("INITIATING_IS_FEATURE_ENABLED"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
        }
      }));

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.warn(LoggerService.getComputedMsg(LoggerService.getInstance().warningMessages.get("CAMPAIGN_NOT_RUNNING"), new HashMap<String, String>() {
          {
            put("campaignKey", campaignKey);
            put("api", APIEnums.API_TYPES.IS_FEATURE_ENABLED.value());
          }
        }));
        return false;
      } else if (
              !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())
                      && !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_TEST.value())
      ) {
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("API_NOT_APPLICABLE"), new HashMap<String, String>() {
          {
            put("api", "isFeatureEnabled");
            put("userId", userId);
            put("campaignKey", campaignKey);
            put("campaignType", campaign.getType());
          }
        }));

        return false;
      }
      
      // if MAB is enabled, then UserStorage must be defined
      if (campaign.getIsMAB() && userStorage == null) {
        // log error message
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages
            .get("NO_USERSTORAGE_WITH_MAB"), new HashMap<String, String>() {
              {
                put("campaignKey", campaign.getKey());
              }
            }));
        return false;
      }

      String variation = ActivateCampaign.activateCampaign(
          APIEnums.API_TYPES.IS_FEATURE_ENABLED.value(), campaign, userId, settingFile,
          variationDecider, isDevelopmentMode, batchEventQueue, CustomVariables,
          variationTargetingVariables, usageStats, clientUserAgent, userIPAddress);

      if (variation == null) {
        return false;
      }


      boolean isFeatureEnabled = false;

      if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        isFeatureEnabled = true;
      } else {
        Variation variationDetails = campaign.getVariations().stream().filter(variationObj -> variationObj.getName().equalsIgnoreCase(variation)).findFirst().get();
        isFeatureEnabled = variationDetails.getIsFeatureEnabled();
      }

      boolean finalIsFeatureEnabled = isFeatureEnabled;
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("FEATURE_STATUS"), new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
          put("status", finalIsFeatureEnabled ? "enabled" : "disabled");
        }
      }));

      return isFeatureEnabled;

    } catch (Exception e) {
      //      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
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

      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("INITIATING_GET_FEATURE_VARIATION"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
          put("variableKey", variableKey);
        }
      }));

      Campaign campaign = settingFile.getCampaign(campaignKey);

      if (campaign == null) {
        LOGGER.warn(LoggerService.getComputedMsg(LoggerService.getInstance().warningMessages.get("CAMPAIGN_NOT_RUNNING"), new HashMap<String, String>() {
          {
            put("campaignKey", campaignKey);
            put("api", APIEnums.API_TYPES.GET_FEATURE_VARIABLE_VALUE.value());
          }
        }));
        return null;
      } else if (
              !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())
                      && !campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_TEST.value())
      ) {
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("API_NOT_APPLICABLE"), new HashMap<String, String>() {
          {
            put("api", "getFeatureVariableValue");
            put("userId", userId);
            put("campaignKey", campaignKey);
            put("campaignType", campaign.getType());
          }
        }));
        return null;
      }

      String variation = CampaignVariation.getCampaignVariationName(settingFile.getSettings(), APIEnums.API_TYPES.GET_FEATURE_VARIABLE_VALUE.value(), campaign, userId, variationDecider,
              CustomVariables, variationTargetingVariables);

      if (variation == null) {
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("FEATURE_STATUS"), new HashMap<String, String>() {
          {
            put("campaignKey", campaign.getKey());
            put("userId", userId);
            put("status", "disabled");
          }
        }));
        return null;
      }

      List<Variable> variables;

      if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value())) {
        variables = campaign.getVariables();
      } else {

        Variation variationDetails = campaign.getVariations().stream().filter(variationObj -> variationObj.getName().equalsIgnoreCase(variation)).findFirst().get();

        if (!variationDetails.getIsFeatureEnabled()) {
          variationDetails = campaign.getVariations().stream().filter(variationObj -> variationObj.getId() == 1).findFirst().get();

          Variation finalVariationDetails = variationDetails;
          LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("FEATURE_VARIABLE_DEFAULT_VALUE"), new HashMap<String, String>() {
            {
              put("variableKey", variableKey);
              put("variationName", finalVariationDetails.getName());
            }
          }));
        }

        variables = variationDetails.getVariables();
      }

      Variable variable = variables.stream().filter(variableObj -> variableObj.getKey().equalsIgnoreCase(variableKey)).findFirst().orElse(null);

      if (variable == null) {
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("VARIABLE_NOT_FOUND"), new HashMap<String, String>() {
          {
            put("variableKey", variableKey);
            put("userId", userId);
          }
        }));
        return null;
      }

      if (variableType == null) {
        variableType = variable.getType().toLowerCase();
      } else if (!variable.getType().equalsIgnoreCase(variableType)) {
        String expectedVariableType = variableType;
        //        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.VARIABLE_REQUESTED_WITH_WRONG_TYPE.value(new HashMap<String, String>() {
        //          {
        //            put("variableType", variable.getType());
        //            put("expectedVariableType", expectedVariableType);
        //          }
        //        }));
        return null;
      }

      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("FEATURE_VARIABLE_VALUE"), new HashMap<String, String>() {
        {
          put("variableKey", variableKey);
          put("campaignKey", campaignKey);
          put("variableValue", variable.getValue().toString());
          put("userId", userId);
        }
      }));

      return FeatureCampaign.getTypeCastedValue(variable.getValue(), variableType);
    } catch (Exception e) {
      //      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return null;
    }
  }

  private static Object getTypeCastedValue(Object value, String type) {
    try {
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
    } catch (Exception e) {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("UNABLE_TO_CAST_VALUE"), new HashMap<String, String>() {
        {
          put("variableValue", value.toString());
          put("variableType", type);
        }
      }));
      return null;
    }

  }
}
