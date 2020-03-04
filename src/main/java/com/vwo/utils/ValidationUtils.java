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

package com.vwo.utils;

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {
  private static final Logger LOGGER = Logger.getLogger(ValidationUtils.class);

  public static boolean isValidParams(Map<String, Object> params, Map<String, Object>... additionalParams) {
    Map<String, Object> loggingParams = additionalParams.length > 0 ? additionalParams[0] : new HashMap<String, Object>() {};
    loggingParams.putAll(params);

    for (Map.Entry<String, Object> param: params.entrySet()) {
      Object value = param.getValue();

      switch (param.getKey()) {
        case "campaignKey":
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_CAMPAIGN_KEY.value(new HashMap<String, String>() {
              {
                put("api", loggingParams.get("api").toString());
              }
            }));
            return false;
          }
          break;
        case "userId":
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_USER_ID.value(new HashMap<String, String>() {
              {
                put("api", loggingParams.get("api").toString());
              }
            }));
            return false;
          }
          break;
        case "goalIdentifier":
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_GOAL_IDENTIFIER.value());
            return false;
          }
          break;
        case "variableKey":
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_VARIABLE_KEY.value());
            return false;
          }
          break;
        case "tagKey":
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_TAG_KEY.value());
            return false;
          }
          if (value.toString().length() > 255) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TAG_KEY_LENGTH_EXCEEDED.value(new HashMap<String, String>() {
              {
                put("tagKey", value.toString());
                put("userId", loggingParams.get("userId").toString());
              }
            }));
            return false;
          }
          break;
        case "tagValue":
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_TAG_VALUE.value());
            return false;
          }
          if (value.toString().length() > 255) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TAG_VALUE_LENGTH_EXCEEDED.value(new HashMap<String, String>() {
              {
                put("tagValue", value.toString());
                put("tagKey", loggingParams.get("tagKey").toString());
                put("userId", loggingParams.get("userId").toString());
              }
            }));
            return false;
          }
          break;
        default:
          if (isEmptyValue(value)) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_PARAM.value());
          }
          return false;
      }
    }
    return true;
  }

  private static boolean isEmptyValue(Object value) {
    return value == null || value.toString().isEmpty();
  }
}
