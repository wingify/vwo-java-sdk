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

package com.vwo.services.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.services.batch.FlushInterface;
import com.vwo.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

public class HttpPostResponseHandler implements ResponseHandler<Void> {
  private static final Logger LOGGER = Logger.getLogger(HttpPostResponseHandler.class);
  private final FlushInterface flushCallback;
  private final JsonNode events;
  private final int accountId;
  private final String endpoint;
  public String error;


  public HttpPostResponseHandler(FlushInterface flushCallback, JsonNode events, int accountId, String endpoint) {
    this.flushCallback = flushCallback;
    this.events = events;
    this.accountId = accountId;
    this.endpoint = endpoint;
  }

  @Override
  public Void handleResponse(HttpResponse response) throws IOException {
    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 && status < 300) {
      LOGGER.debug(LoggerMessagesEnums.INFO_MESSAGES.BULK_IMPRESSION_SUCCESS.value(new HashMap<String, String>() {
        {
          put("a", String.valueOf(accountId));
          put("endPoint", HttpUtils.getModifiedLogRequest(endpoint));
        }
      }));
      if (flushCallback != null) {
        flushCallback.onFlush(null, events);
      }
    } else if (status == 413) {
      error = EntityUtils.toString(response.getEntity());
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.BATCH_EVENT_LIMIT_EXCEEDED.value(new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
          put("endPoint", endpoint);
          put("eventsPerRequest", String.valueOf(events.get("ev").size()));
        }
      }));
      LOGGER.debug(LoggerMessagesEnums.ERROR_MESSAGES.IMPRESSION_FAILED.value(new HashMap<String, String>() {
        {
          put("endPoint", endpoint);
          put("err", error);
        }
      }));
      if (flushCallback != null) {
        flushCallback.onFlush(error, events);
      }
    } else {
      error = EntityUtils.toString(response.getEntity());
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.BULK_NOT_PROCESSED.value());
      LOGGER.debug(LoggerMessagesEnums.ERROR_MESSAGES.IMPRESSION_FAILED.value(new HashMap<String, String>() {
        {
          put("endPoint", endpoint);
          put("err", error);
        }
      }));
      if (flushCallback != null) {
        flushCallback.onFlush(error, events);
      }
    }
    return null;
  }
}
