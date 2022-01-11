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

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
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

  public HttpGetRequest(HttpParams httpParams) {
    this.httpParams = httpParams;
  }

  @Override
  public void run() {
    try {
      getRequest(httpParams);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.HTTP_REQUEST_EXCEPTION.value(), e);
    }
  }

  private void getRequest(HttpParams httpParams) throws IOException, URISyntaxException {
    URI httpUri = HttpUtils.getRequestUri(httpParams);
    HttpRequestBase request = new HttpGet(httpUri);

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.HTTP_REQUEST_EXECUTED.value(new HashMap<String, String>() {
      {
        put("url", HttpUtils.getModifiedLogRequest(request.getURI().toString()));
      }
    }));

    httpClient.send(request, new HttpGetResponseHandler());
  }

  public static HttpGetRequest send(HttpParams httpParams) {
    try {
      HttpGetRequest httpGetRequest = new HttpGetRequest(httpParams);
      new Thread(httpGetRequest).start();
      return httpGetRequest;
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value(), e);
      return null;
    }
  }
}
