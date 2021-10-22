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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.utils.HttpUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HttpPostRequest implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(HttpPostRequest.class);

  private final HttpClient httpClient = new HttpClient();
  private final HttpParams httpParams;
  private final PostResponseHandler responseHandler;

  public HttpPostRequest(HttpParams httpParams, PostResponseHandler responseHandler) {
    this.httpParams = httpParams;
    this.responseHandler = responseHandler;
  }

  @Override
  public void run() {
    try {
      postRequest(httpParams);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.HTTP_REQUEST_EXCEPTION.value(), e);
    }
  }

  /**
   * Creates a URI and Sends POST network call to VWO servers.
   *
   * @param httpParams Params for the network call.
   * @return Boolean indicating whether the call was successful or not.
   * @throws IOException        IOException
   * @throws URISyntaxException URISyntaxException
   */
  private boolean postRequest(HttpParams httpParams) throws IOException, URISyntaxException {
    URI httpUri = HttpUtils.getRequestUri(httpParams);
    HttpPost request = new HttpPost(httpUri);
    StringEntity params = new StringEntity(httpParams.getBody());
    request.setEntity(params);
    request.setHeaders(httpParams.getHeaders());
    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.HTTP_REQUEST_EXECUTED.value(new HashMap<String, String>() {
      {
        put("url", HttpUtils.getModifiedLogRequest(request.getURI().toString()));
      }
    }));

    HttpPostResponseHandler response = new HttpPostResponseHandler(
            responseHandler,
            new ObjectMapper().readTree(httpParams.getBody()),
            request.getURI().toString());

    httpClient.send(request, response);
    return response.error == null;
  }

  /**
   * Sends a POST network call to VWO servers in sync and async mode.
   *
   * @param httpParams      Params for the network call
   * @param sendSyncRequest Boolean value to decide whether the call should be sync or async
   * @return Boolean indicating whether the call was successful or not.
   */
  public static boolean send(HttpParams httpParams, PostResponseHandler responseHandler, boolean sendSyncRequest) {
    try {
      HttpPostRequest httpPostRequest = new HttpPostRequest(httpParams, responseHandler);
      if (sendSyncRequest) {
        return httpPostRequest.postRequest(httpPostRequest.httpParams);
      } else {
        new Thread(httpPostRequest).start();
        return true;
      }
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value(), e);
      return false;
    }
  }
}
