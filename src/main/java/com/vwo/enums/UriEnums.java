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

package com.vwo.enums;

public enum UriEnums {

  PROTOCOL("https"),
  SDK_VERSION("1.8.2"),
  BASE_URL("dev.visualwebsiteoptimizer.com"),
  ACCOUNT_SETTINGS("/server-side/settings"),
  TRACK_USER("/server-side/track-user"),
  TRACK_GOAL("/server-side/track-goal"),
  PUSH("/server-side/push");

  private final String uri;

  UriEnums(String uri) {
    this.uri = uri;
  }

  @Override
  public String toString() {
    return this.uri;
  }
}
