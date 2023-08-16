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
import com.vwo.enums.EventArchEnums;
import com.vwo.logger.LoggerService;
import com.vwo.models.response.BatchEventData;
import com.vwo.services.batch.BatchEventQueue;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpGetRequest;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.services.http.HttpPostRequest;
import com.vwo.services.settings.SettingFile;
import com.vwo.logger.Logger;
import com.vwo.utils.HttpUtils;
import com.vwo.utils.ValidationUtils;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Segmentation {
  private static final Logger LOGGER = Logger.getLogger(Segmentation.class);

  /**
   * Pushes the tag key and value for a user to be used in post segmentation.
   *
   * @param settingFile       Settings file Configuration
   * @param tagKey            Tag name
   * @param tagValue          Tag value
   * @param userId            ID assigned to a user
   * @param batchEventQueue   Event Batching Queue.
   * @param isDevelopmentMode Development mode flag.
   * @return boolean value
   */
  public static Map<String, Boolean> pushCustomDimension(SettingFile settingFile, String tagKey, String tagValue, String userId, BatchEventQueue batchEventQueue,
                                                         boolean isDevelopmentMode, Map<String, String> customDimensionMap, Map<String, Integer> usageStats) {
    try {

      Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
      if (tagKey.equals(" ") && tagValue.equals(" ") && (customDimensionMap == null || customDimensionMap.size() == 0)) {
        //        LOGGER.error(LoggerService.getInstance().errorMessages.get("PUSH_INVALID_PARAMS_CD_MAP"));
        return resultMap;
      }

      if (!tagKey.equals(" ") && !tagValue.equals(" ")) {
        customDimensionMap.put(tagKey, tagValue);
      }

      Map<String, String> cdMap = new HashMap<String, String>(customDimensionMap);
      for (Map.Entry<String, String> customMap : cdMap.entrySet()) {
        if (!ValidationUtils.isValidParams(
            new HashMap<String, Object>() {
              {
                put("tagKey", customMap.getKey());
                put("tagValue", customMap.getValue());
                put("userId", userId);
              }
            },
            APIEnums.API_TYPES.PUSH
        )) {
          resultMap.put(customMap.getKey(), false);
          customDimensionMap.remove(customMap.getKey());
          continue;
        }

        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("INITIATING_PUSH_DIMENSION"), new HashMap<String, String>() {
          {
            put("tagKey", customMap.getKey());
            put("tagValue", customMap.getValue());
            put("userId", userId);
          }
        }));
      }

      return Segmentation.sendPostCustomDimensionCall(settingFile, userId, batchEventQueue, isDevelopmentMode, customDimensionMap, usageStats, resultMap);
    } catch (Exception e) {
      //      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      return new HashMap<String, Boolean>();
    }
  }

  private static Map<String, Boolean> sendPostCustomDimensionCall(SettingFile settingFile, String userId, BatchEventQueue batchEventQueue,
                                                                  boolean isDevelopmentMode, Map<String, String> customDimensionMap, Map<String, Integer> usageStats, Map<String, Boolean> result) {

    try {
      if (!isDevelopmentMode) {
        if (batchEventQueue != null) {
          for (Map.Entry<String, String> query : customDimensionMap.entrySet()) {
            result.put(query.getKey(), true);
            batchEventQueue.enqueue(HttpRequestBuilder.getBatchEventForCustomDimension(settingFile, query.getKey(), query.getValue(), userId));
          }
        } else if (settingFile.getSettings().getIsEventArchEnabled() != null && settingFile.getSettings().getIsEventArchEnabled()) {
          for (Map.Entry<String, String> query : customDimensionMap.entrySet()) {
            result.put(query.getKey(), true);
          }
          Map<String, Object> pushPayload = HttpRequestBuilder.getEventArchPushPayload(settingFile, userId, customDimensionMap);
          HttpParams httpParams = HttpRequestBuilder.getEventArchQueryParams(settingFile, EventArchEnums.VWO_SYN_VISITOR_PROP.toString(), pushPayload, null, null, null);
          HttpPostRequest.send(httpParams, HttpUtils.handleEventArchResponse(settingFile.getSettings().getAccountId(), null, customDimensionMap), false);
        } else {

          if (customDimensionMap.size() > 1) {
            BatchEventData batchEventData = new BatchEventData();
            batchEventData.setEventsPerRequest(customDimensionMap.size());
            batchEventQueue = new BatchEventQueue(batchEventData, settingFile.getSettings().getSdkKey(), settingFile.getSettings().getAccountId(), isDevelopmentMode, usageStats);

          }

          for (Map.Entry<String, String> query : customDimensionMap.entrySet()) {
            result.put(query.getKey(), true);
            if (customDimensionMap.size() == 1) {
              HttpParams httpParams = HttpRequestBuilder.getCustomDimensionParams(settingFile, query.getKey(), query.getValue(), userId);
              HttpGetRequest.send(httpParams, settingFile.getSettings().getAccountId());
            } else {
              batchEventQueue.enqueue(HttpRequestBuilder.getBatchEventForCustomDimension(settingFile, query.getKey(), query.getValue(), userId));
            }
          }
        }
      }
    } catch (Exception e) {
      // LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
    }

    return result;
  }
}
