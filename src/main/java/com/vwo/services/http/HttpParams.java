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

import com.vwo.enums.HTTPEnums;

import java.util.Map;

public class HttpParams {

  private final String domain;
  private final String url;
  private final Map<String, Object> queryParams;
  private final HTTPEnums.Verbs httpVerb;

  public HttpParams(String domain, String url, Map<String, Object> queryParams, HTTPEnums.Verbs httpVerb) {
    this.domain = domain;
    this.url = url;
    this.queryParams = queryParams;
    this.httpVerb = httpVerb;
  }

  public String getDomain() {
    return domain;
  }

  public String getUrl() {
    return url;
  }

  public Map<String, Object> getQueryParams() {
    return queryParams;
  }

  public HTTPEnums.Verbs getHttpVerb() {
    return httpVerb;
  }
}
