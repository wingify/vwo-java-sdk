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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

public class HttpPostResponseHandler implements ResponseHandler<Void> {
  private final JsonNode properties;
  private final String endpoint;
  public String error;
  public PostResponseHandler responseHandler;

  public HttpPostResponseHandler(PostResponseHandler responseHandler, JsonNode properties, String endpoint) {
    this.properties = properties;
    this.endpoint = endpoint;
    this.responseHandler = responseHandler;
  }

  @Override
  public Void handleResponse(HttpResponse response) throws IOException {
    int status = response.getStatusLine().getStatusCode();
    responseHandler.onResponse(endpoint, status, response, properties);
    return null;
  }
}
