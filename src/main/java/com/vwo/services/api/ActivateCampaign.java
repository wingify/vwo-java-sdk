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

import com.vwo.enums.EventArchEnums;
import com.vwo.enums.APIEnums;
import com.vwo.enums.CampaignEnums;
import com.vwo.logger.LoggerService;
import com.vwo.services.batch.BatchEventQueue;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.settings.SettingFile;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpGetRequest;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.services.http.HttpPostRequest;
import com.vwo.logger.Logger;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Variation;
import com.vwo.utils.CampaignUtils;
import com.vwo.utils.HttpUtils;
import com.vwo.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class ActivateCampaign {
  private static final Logger LOGGER = Logger.getLogger(ActivateCampaign.class);

  /**
   * Get variation and track conversion on VWO server.
   *
   * @param campaignKey                 Campaign key
   * @param userId                      User ID
   * @param settingFile                 Settings file Configuration
   * @param variationDecider            Variation decider service
   * @param isDevelopmentMode           Development mode flag.
   * @param batchEventQueue             Event Batching Queue.
   * @param usageStats                  usage info collected at the time of VWO instantiation.
   * @param CustomVariables             Pre Segmentation custom variables
   * @param variationTargetingVariables User Whitelisting Targeting variables
   * @param clientUserAgent        User Agent of the visitor
   * @param userIPAddress               IP of the visitor
   * @return String variation name, or null if the user doesn't qualify to become a part of the campaign.
   */
  public static String activate(
          String campaignKey,
          String userId,
          SettingFile settingFile,
          VariationDecider variationDecider,
          boolean isDevelopmentMode,
          BatchEventQueue batchEventQueue,
          Map<String, Integer> usageStats,
          Map<String, ?> CustomVariables,
          Map<String, ?> variationTargetingVariables,
          String clientUserAgent,
          String userIPAddress
  ) {
    return activateImpl(campaignKey, userId, settingFile, variationDecider,
      isDevelopmentMode, batchEventQueue, usageStats, CustomVariables,
      variationTargetingVariables, clientUserAgent, userIPAddress);
  }

  // validate activate params and activate campaign
  public static String activateImpl(
          String campaignKey,
          String userId,
          SettingFile settingFile,
          VariationDecider variationDecider,
          boolean isDevelopmentMode,
          BatchEventQueue batchEventQueue,
          Map<String, Integer> usageStats,
          Map<String, ?> CustomVariables,
          Map<String, ?> variationTargetingVariables,
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
              APIEnums.API_TYPES.ACTIVATE
      )) {
        return null;
      }

      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("INITIATING_ACTIVATE"), new HashMap<String, String>() {
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
            put("api", APIEnums.API_TYPES.ACTIVATE.value());
          }
        }));
        return null;
      } else if (campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_ROLLOUT.value()) || campaign.getType().equalsIgnoreCase(CampaignEnums.CAMPAIGN_TYPES.FEATURE_TEST.value())) {
        LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("API_NOT_APPLICABLE"), new HashMap<String, String>() {
          {
            put("api", "activate");
            put("userId", userId);
            put("campaignKey", campaignKey);
            put("campaignType", campaign.getType());
          }
        }));
        return null;
      }

      return ActivateCampaign.activateCampaign(APIEnums.API_TYPES.ACTIVATE.value(), campaign, userId, settingFile, variationDecider, isDevelopmentMode,
              batchEventQueue, CustomVariables, variationTargetingVariables, usageStats, clientUserAgent, userIPAddress);
    } catch (Exception e) {
      // LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return null;
    }
  }

  public static String activateCampaign(
          String apiName,
          Campaign campaign,
          String userId,
          SettingFile settingFile,
          VariationDecider variationDecider,
          boolean isDevelopmentMode,
          BatchEventQueue batchEventQueue,
          Map<String, ?> CustomVariables,
          Map<String, ?> variationTargetingVariables,
          Map<String, Integer> usageStats,
          String clientUserAgent,
          String userIPAddress
  ) {
    String variation = CampaignVariation.getCampaignVariationName(settingFile.getSettings(), apiName, campaign, userId, variationDecider,
            CustomVariables, variationTargetingVariables, null);

    if (variation != null) {

      if (variationDecider.getIsStoredVariation()) {
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("CAMPAIGN_USER_ALREADY_TRACKED"), new HashMap<String, String>() {
          {
            put("campaignKey", campaign.getKey());
            put("userId", userId);
            put("api", apiName);
          }
        }));
      } else {
        //        LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.ACTIVATING_CAMPAIGN.value(new HashMap<String, String>() {
        //          {
        //            put("userId", userId);
        //            put("variation", variation);
        //          }
        //        }));
        // Send Impression Call for Stats
        ActivateCampaign.sendUserCall(settingFile, campaign, userId, batchEventQueue,
            CampaignUtils.getVariationObjectFromCampaign(campaign, variation), isDevelopmentMode,
            usageStats, clientUserAgent, userIPAddress);
      }
    } else {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("DECISION_NO_VARIATION_ALLOTED"), new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaign.getKey());
        }
      }));
    }

    return variation;
  }

  private static void sendUserCall(SettingFile settingFile, Campaign campaign, String userId, BatchEventQueue batchEventQueue,
                                   Variation variation, boolean isDevelopmentMode,
                                   Map<String, Integer> usageStats, String clientUserAgent,
                                   String userIPAddress) {
    try {
      // event batching enabled
      if (batchEventQueue != null) {
        batchEventQueue.enqueue(HttpRequestBuilder.getBatchEventForTrackingUser(settingFile, campaign, userId, variation, clientUserAgent, userIPAddress));
      } else if (settingFile.getSettings().getIsEventArchEnabled() != null && settingFile.getSettings().getIsEventArchEnabled()) {
        // event batching not enabled, but event arch enabled
        if (!isDevelopmentMode) {
          Map<String, Object> trackUserPayload = HttpRequestBuilder.getEventArchTrackUserPayload(settingFile, userId, campaign.getId(), variation.getId(), clientUserAgent, userIPAddress);
          HttpParams httpParams = HttpRequestBuilder.getEventArchQueryParams(settingFile, EventArchEnums.VWO_VARIATION_SHOWN.toString(), trackUserPayload, usageStats, clientUserAgent,
              userIPAddress);
          HttpPostRequest.send(httpParams, HttpUtils.handleEventArchResponse(settingFile.getSettings().getAccountId(), EventArchEnums.VWO_VARIATION_SHOWN.toString(), null), false);
        }
      } else {
        // event batching not enabled, event arch not enabled
        HttpParams httpParams = HttpRequestBuilder.getUserParams(settingFile, campaign, userId, variation, usageStats, clientUserAgent, userIPAddress);
        if (!isDevelopmentMode) {
          HttpGetRequest.send(httpParams, settingFile.getSettings().getAccountId());
        }
      }
    } catch (Exception e) {
      // LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
    }
  }
}
