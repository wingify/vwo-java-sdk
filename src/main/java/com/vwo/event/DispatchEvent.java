/*
Copyright 2019 Wingify Software Pvt. Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.vwo.event;

import java.util.Map;

/*
 This file DispatchEvent.java has references from "Optimizely Java SDK, version 3.2.0", Copyright 2017-2019, Optimizely,
 under Apache 2.0 License.
 Source - https://github.com/optimizely/java-sdk/blob/master/core-api/src/main/java/com/optimizely/ab/event/LogEvent.java
*/
public class DispatchEvent {

  private final String host;
  private final String path;
  private final Map<String, Object> requestParams;
  private final RequestMethod requestMethod;
  private final String body;

  public DispatchEvent(String host, String path, Map<String, Object> requestParams, RequestMethod requestMethod, String body) {
    this.host = host;
    this.path = path;
    this.requestParams = requestParams;
    this.requestMethod = requestMethod;
    this.body = body;
  }

  public String getHost() {
    return host;
  }

  public String getPath() {
    return path;
  }

  public Map<String, Object> getRequestParams() {
    return requestParams;
  }

  public RequestMethod getRequestMethod() {
    return requestMethod;
  }

  public String getBody() {
    return body;
  }

  public enum RequestMethod {
    GET,
    POST
  }
}
