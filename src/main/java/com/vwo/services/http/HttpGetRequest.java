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

package com.vwo.services.http;

import com.vwo.logger.Logger;
import com.vwo.logger.LoggerService;
import com.vwo.utils.HttpUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HttpGetRequest implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(HttpGetRequest.class);

  private final HttpClient httpClient = new HttpClient();
  private final HttpParams httpParams;
  private final int accountId;

  public HttpGetRequest(HttpParams httpParams, int accountId) {
    this.httpParams = httpParams;
    this.accountId = accountId;
  }

  @Override
  public void run() {
    try {
      getRequest(httpParams);
    } catch (Exception e) {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("IMPRESSION_FAILED"), new HashMap<String, String>() {
        {
          put("endPoint", httpParams.getUrl());
          put("err", e.getLocalizedMessage());
        }
      }));
    }
  }

  private void getRequest(HttpParams httpParams) throws IOException, URISyntaxException {
    URI httpUri = HttpUtils.getRequestUri(httpParams);
    HttpRequestBase request = new HttpGet(httpUri);

    //    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.HTTP_REQUEST_EXECUTED.value(new HashMap<String, String>() {
    //      {
    //        put("url", HttpUtils.getModifiedLogRequest(request.getURI().toString()));
    //      }
    //    }));

    LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("EVENT_QUEUE"), new HashMap<String, String>() {
      {
        put("event", HttpUtils.getModifiedLogRequest(request.getURI().toString()));
        put("queueType", "normal");
      }
    }));

    httpClient.send(request, new HttpGetResponseHandler(accountId, HttpUtils.getModifiedLogRequest(request.getURI().toString())));
  }

  public static HttpGetRequest send(HttpParams httpParams, int accountId) {
    try {
      HttpGetRequest httpGetRequest = new HttpGetRequest(httpParams, accountId);
      new Thread(httpGetRequest).start();
      return httpGetRequest;
    } catch (Exception e) {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("IMPRESSION_FAILED"), new HashMap<String, String>() {
        {
          put("endPoint", httpParams.getUrl());
          put("err", e.getLocalizedMessage());
        }
      }));
      return null;
    }
  }
}
