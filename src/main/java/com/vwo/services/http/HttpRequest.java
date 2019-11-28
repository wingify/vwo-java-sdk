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

public class HttpRequest implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(HttpRequest.class);

  private final HttpClient httpClient = new HttpClient();
  private final HttpParams httpParams;

  public HttpRequest(HttpParams httpParams) {
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
    URI httpUri = HttpUtils.getHttpUri(httpParams);
    HttpRequestBase request = new HttpGet(httpUri);

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.HTTP_REQUEST_EXECUTED.value(new HashMap<String, String>() {
      {
        put("url", request.getURI().toString());
      }
    }));

    httpClient.send(request, new HttpResponseHandler());
  }

  public static HttpRequest send(HttpParams httpParams) {
    try {
      HttpRequest httpRequest = new HttpRequest(httpParams);
      new Thread(httpRequest).start();
      return httpRequest;
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value(), e);
      return null;
    }
  }
}
