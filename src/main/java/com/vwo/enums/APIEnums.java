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

public class APIEnums {
  public enum API_TYPES {
    ACTIVATE("activate"),
    GET_VARIATION_NAME("getVariationName"),
    IS_FEATURE_ENABLED("isFeatureEnabled"),
    GET_FEATURE_VARIABLE_VALUE("getFeatureVariableValue"),
    PUSH("push"),
    FLUSH_EVENTS("flushEvents"),
    UPDATE_SETTINGS_FILE("getAndUpdateSettingsFile"),
    TRACK("track");

    private final String type;

    API_TYPES(String type) {
      this.type = type;
    }

    public String value() {
      return this.type;
    }
  }
  
  public enum VISITOR {
    USERAGENT("visitor_ua"),
    CUSTOMHEADER_USERAGENT("X-Device-User-Agent"),
    IP("visitor_ip"),
    CUSTOMHEADER_IP("VWO-X-Forwarded-For");

    private final String visitor;
    
    VISITOR(String visitor) {
      this.visitor = visitor;
    }
    
    public String value() {
      return this.visitor;
    }
  }
}
