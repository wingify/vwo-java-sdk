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

import com.vwo.enums.HTTPEnums;
import com.vwo.services.batch.FlushInterface;
import org.apache.http.Header;

import java.util.List;
import java.util.Map;

public class HttpParams {

  private final String domain;
  private final String url;
  private final Map<String, Object> queryParams;
  private Header[] headers;
  private String body;
  private final HTTPEnums.Verbs httpVerb;
  private FlushInterface flushCallback;

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

  public Header[] getHeaders() {
    return this.headers;
  }

  public void setHeaders(Header[] headers) {
    this.headers = headers;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  public FlushInterface getFlushCallback() {
    return flushCallback;
  }

  public void setFlushCallback(FlushInterface flushCallback) {
    this.flushCallback = flushCallback;
  }
}
