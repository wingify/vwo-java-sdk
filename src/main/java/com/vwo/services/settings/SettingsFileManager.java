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

package com.vwo.services.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.enums.UriEnums;
import com.vwo.services.http.HttpClient;
import com.vwo.logger.Logger;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.utils.HttpUtils;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

/**
 * File Setting Utility Class to provide the settingsFile which helps in instantiating the VWO CLIENT INSTANCE.
 */
public class SettingsFileManager {

  private static final Logger LOGGER = Logger.getLogger(SettingsFileManager.class);

  /**
   * Fetches account settings from VWO server.
   *
   * @param accountID VWO application account-id.
   * @param sdkKey    Unique sdk-key
   * @return campaign settings
   */
  public static String getSettingsFile(String accountID, String sdkKey) {
    if (accountID == null || accountID.isEmpty() || sdkKey == null || sdkKey.isEmpty()) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.MISSING_IMPORT_SETTINGS_MANDATORY_PARAMS.toString());
      return null;
    }

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.FETCHING_ACCOUNT_SETTINGS.value(new HashMap<String, String>() {
      {
        put("accountID", accountID);
      }
    }));

    HttpParams httpParams = HttpRequestBuilder.getSettingParams(accountID, sdkKey);
    HttpClient httpClient = new HttpClient();
    JsonNode jsonNode = null;

    try {
      URI httpUri = HttpUtils.getHttpUri(httpParams);
      HttpGet httpRequest = new HttpGet(httpUri);

      httpRequest.setHeader("Content-Type", "application/json");
      httpRequest.setHeader("charset", "UTF-8");

      try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpRequest)) {
        if (closeableHttpResponse != null) {
          try (InputStream content = closeableHttpResponse.getEntity().getContent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readValue(content, JsonNode.class);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
              String stringifiedJsonNode = String.valueOf(jsonNode);
              LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.ACCOUNT_SETTINGS_NOT_FOUND.value(new HashMap<String, String>() {
                {
                  put("statusCode", String.valueOf(statusCode));
                  put("message", stringifiedJsonNode);
                }
              }));
            }
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Something went wrong:", e);
    }

    return String.valueOf(parseSettings(jsonNode));
  }

  public static JsonNode parseSettings(JsonNode jsonNode) {
    ArrayList<String> fieldsToParseAsArray = new ArrayList<>(Arrays.asList(
        "campaigns",
        "campaigns.goals",
        "campaigns.variations",
        "campaigns.variables",
        "campaigns.variations.variables"
    ));

    recursivelyParseNodes(jsonNode, fieldsToParseAsArray);
    return jsonNode;
  }

  private static void recursivelyParseNodes(JsonNode jsonNode, ArrayList<String> fieldsToParseAsArray) {
    if (fieldsToParseAsArray.size() == 0) {
      return;
    }

    Iterator<Map.Entry<String, JsonNode>> jsonFields = jsonNode.fields();

    while (jsonFields.hasNext()) {
      Map.Entry<String, JsonNode> entry = jsonFields.next();
      String key = entry.getKey().toLowerCase();

      if (fieldsToParseAsArray.contains(key)) {
        if (!entry.getValue().isArray()) {
          parseValueWithCorrectType(entry);
        } else {
          for (JsonNode subJsonNode: entry.getValue()) {
            ArrayList<String> subFieldsToParseArray = (ArrayList<String>) fieldsToParseAsArray.stream()
                .filter(field -> field.contains(key + "."))
                .map(field -> field.replaceFirst(key + ".", ""))
                .collect(Collectors.toList());
            recursivelyParseNodes(subJsonNode, subFieldsToParseArray);
          }
        }
      }
    }
  }

  private static void parseValueWithCorrectType(Map.Entry<String, JsonNode> entry) {
    try {
      if (!entry.getValue().isArray()) {
        entry.setValue(new ObjectMapper().createArrayNode());
      }
    } catch (Exception e) {
      // Leave the node without changes.
    }
  }
}
