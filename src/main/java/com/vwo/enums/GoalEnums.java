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

public class GoalEnums {

  public enum GOAL_TYPES {
    REVENUE("REVENUE_TRACKING"),
    CUSTOM("CUSTOM_GOAL"),
    ALL("ALL");

    private final String type;

    GOAL_TYPES(String type) {
      this.type = type;
    }

    public String value() {
      return this.type;
    }
  }
  
  public enum MCA_TYPE {
    REVENUE_PROP(-1);

    private final int mca;

    MCA_TYPE(int mca) {
      this.mca = mca;
    }

    public int value() {
      return this.mca;
    }
  }
}
