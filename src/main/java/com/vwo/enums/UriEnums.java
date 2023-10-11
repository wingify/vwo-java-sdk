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

package com.vwo.enums;

public enum UriEnums {

  PROTOCOL("https"),
  SDK_VERSION("1.62.0"),
  SDK_NAME("java"),
  BASE_URL("dev.visualwebsiteoptimizer.com"),
  SETTINGS_URL("/server-side/settings"),
  WEBHOOK_SETTINGS_URL("/server-side/pull"),
  TRACK_USER("/server-side/track-user"),
  TRACK_GOAL("/server-side/track-goal"),
  PUSH("/server-side/push"),
  BATCH_EVENTS("/server-side/batch-events"),
  EVENTS("/events/t");

  private final String uri;

  UriEnums(String uri) {
    this.uri = uri;
  }

  @Override
  public String toString() {
    return this.uri;
  }
}
