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

import com.fasterxml.jackson.databind.JsonNode;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.enums.UriEnums;
import com.vwo.logger.Logger;
import com.vwo.services.api.TrackCampaign;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.PostResponseHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
  private static final Logger LOGGER = Logger.getLogger(TrackCampaign.class);

  public static URI getRequestUri(HttpParams httpParams) throws URISyntaxException {
    URIBuilder requestBuilder = new URIBuilder();
    requestBuilder.setScheme(UriEnums.PROTOCOL.toString())
            .setHost(httpParams.getDomain())
            .setPath(httpParams.getUrl());

    // Remove null values from request params
    for (Map.Entry<String, Object> query : httpParams.getQueryParams().entrySet()) {
      if (query.getValue() != null) {
        requestBuilder.addParameter(query.getKey(), query.getValue().toString());
      }
    }

    return requestBuilder.build();
  }

  public static String getModifiedLogRequest(String queryString) {
    return queryString.replaceAll("(&env=.{32})", "");
  }

  public static Map<String, Object> attachUsageStats(Map<String, Object> map, Map<String, Integer> usageStatsEntry) {
    if (usageStatsEntry != null && !usageStatsEntry.isEmpty()) {
      for (Map.Entry<String, Integer> mapElement : usageStatsEntry.entrySet()) {
        map.put(mapElement.getKey(), mapElement.getValue());
      }
    }

    return map;
  }

  /**
   * Removes null values from the map.
   * @param urlMap A map containing null/non-null values
   * @return Map containing non-null values.
   */
  public static Map<String, Object> removeNullValues(Map<String, Object> urlMap) {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    for (Map.Entry<String, Object> query : urlMap.entrySet()) {
      if (query.getValue() != null) {
        if (query.getKey().equals("visitor")) {
          requestMap.put("$visitor", query.getValue());
        } else {
          requestMap.put(query.getKey(), query.getValue());
        }
      }
    }
    return requestMap;
  }

  /**
   * Handles the response of the network call, print required logs.
   * @param accountId Account id
   * @param eventName Name of the event
   * @param customEvents Map containing custom events
   * @return PostResponseHandler instance.
   */
  public static PostResponseHandler handleEventArchResponse(int accountId, String eventName, Map<String, String> customEvents) {
    return new PostResponseHandler() {
      @Override
      public void onResponse(String endpoint, int status, HttpResponse response, JsonNode properties) throws IOException {
        try {
          if (status >= 200 && status < 300) {
            LOGGER.debug(LoggerMessagesEnums.INFO_MESSAGES.EVENT_ARCH_IMPRESSION_SUCCESS.value(new HashMap<String, String>() {
              {
                put("a", String.valueOf(accountId));
                put("endPoint", HttpUtils.getModifiedLogRequest(endpoint));
                put("event", customEvents == null
                    ? eventName + " event"
                    : "visitor properties: " + customEvents.toString());
              }
            }));
          } else {
            String error = EntityUtils.toString(response.getEntity());
            LOGGER.debug(LoggerMessagesEnums.ERROR_MESSAGES.IMPRESSION_FAILED.value(new HashMap<String, String>() {
              {
                put("endPoint", endpoint);
                put("err", error);
              }
            }));
          }
        } catch (Exception e) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
        }
      }
    };
  }
}
