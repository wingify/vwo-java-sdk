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

package com.vwo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.APIEnums;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {
  private static final Logger LOGGER = Logger.getLogger(ValidationUtils.class);

  public static boolean isValidParams(Map<String, Object> params, APIEnums.API_TYPES api) {
    switch (api) {
      case ACTIVATE:
        if (!ValidationUtils.areBasicChecksValid(params)) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.ACTIVATE_API_MISSING_PARAMS.value());
          return false;
        }
        break;
      case GET_VARIATION_NAME:
        if (!ValidationUtils.areBasicChecksValid(params)) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GET_VARIATION_API_MISSING_PARAMS.value());
          return false;
        }
        break;
      case IS_FEATURE_ENABLED:
        if (!ValidationUtils.areBasicChecksValid(params)) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.IS_FEATURE_ENABLED_API_MISSING_PARAMS.value());
          return false;
        }
        break;
      case GET_FEATURE_VARIABLE_VALUE:
        if (!ValidationUtils.areBasicChecksValid(params)) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GET_FEATURE_VARIABLE_MISSING_PARAMS.value());
          return false;
        }
        break;
      case PUSH:
        if (!ValidationUtils.areBasicChecksValid(params)) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.PUSH_API_INVALID_PARAMS.value());
          return false;
        }
        break;
      case TRACK:
        if (!ValidationUtils.areBasicChecksValid(params, api)) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TRACK_API_MISSING_PARAMS.value());
          return false;
        }
        break;
      default:
        return false;
    }
    return true;
  }

  private static boolean areBasicChecksValid(Map<String, Object> params) {
    return ValidationUtils.areBasicChecksValid(params, null);
  }

  private static boolean areBasicChecksValid(Map<String, Object> params, APIEnums.API_TYPES api) {
    for (Map.Entry<String, Object> param: params.entrySet()) {
      Object value = param.getValue();

      switch (param.getKey()) {
        case "campaignKey":
          if (api != null && api.equals(APIEnums.API_TYPES.TRACK)) {
            if (!(value == null || (value instanceof String && !value.toString().isEmpty()) || (value instanceof String[] && ((String[]) value).length != 0))) {
              return false;
            }
          } else if (isEmptyValue(value)) {
            return false;
          }
          break;
        case "userId":
        case "goalIdentifier":
        case "variableKey":
          if (isEmptyValue(value)) {
            return false;
          }
          break;
        case "tagKey":
          if (isEmptyValue(value)) {
            return false;
          }
          if (value.toString().length() > 255) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TAG_KEY_LENGTH_EXCEEDED.value(new HashMap<String, String>() {
              {
                put("tagKey", value.toString());
                put("userId", params.get("userId").toString());
              }
            }));
            return false;
          }
          break;
        case "tagValue":
          if (isEmptyValue(value)) {
            return false;
          }
          if (value.toString().length() > 255) {
            LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.TAG_VALUE_LENGTH_EXCEEDED.value(new HashMap<String, String>() {
              {
                put("tagValue", value.toString());
                put("tagKey", params.get("tagKey").toString());
                put("userId", params.get("userId").toString());
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

  /**
   * Validate the Json Passed.
   * @param json Json in string format
   * @return True if json is valid, else false.
   */
  public static boolean isValidJSON(final String json) {
    boolean valid = false;
    try {
      new ObjectMapper().readValue(json, Map.class);
      valid = true;
    } catch (JsonProcessingException e) {
      valid = false;
    }
    return valid;
  }
}
