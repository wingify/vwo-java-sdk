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

package com.vwo.utils;

import com.vwo.enums.UriEnums;
import com.vwo.services.http.HttpParams;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpUtils {

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
}
