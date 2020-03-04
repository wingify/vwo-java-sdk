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

package com.vwo.services.http;

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.util.HashMap;

public class HttpResponseHandler implements ResponseHandler<Void> {
  private static final Logger LOGGER = Logger.getLogger(HttpResponseHandler.class);

  @Override
  public Void handleResponse(HttpResponse response) throws IOException {
    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 && status < 300) {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.HTTP_RESPONSE.value(new HashMap<String, String>() {
        {
          put("response", response.getStatusLine().toString());
        }
      }));
      return null;
    } else {
      throw new ClientProtocolException("Unexpected response code received: " + status);
    }
  }
}
