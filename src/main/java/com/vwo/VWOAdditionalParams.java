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

package com.vwo;

import com.vwo.enums.GoalEnums;

import java.util.Map;

public class VWOAdditionalParams {
  private Map<String, ?> customVariables;
  private Map<String, ?> variationTargetingVariables;
  private Object revenueValue;
  GoalEnums.GOAL_TYPES goalTypeToTrack;

  public Map<String, ?> getCustomVariables() {
    return customVariables;
  }

  public VWOAdditionalParams setCustomVariables(Map<String, ?> customVariables) {
    this.customVariables = customVariables;
    return this;
  }

  public Map<String, ?> getVariationTargetingVariables() {
    return variationTargetingVariables;
  }

  public VWOAdditionalParams setVariationTargetingVariables(Map<String, ?> variationTargetingVariables) {
    this.variationTargetingVariables = variationTargetingVariables;
    return this;
  }

  public Object getRevenueValue() {
    return revenueValue;
  }

  public VWOAdditionalParams setRevenueValue(Object revenueValue) {
    this.revenueValue = revenueValue;
    return this;
  }

  public GoalEnums.GOAL_TYPES getGoalTypeToTrack() {
    return goalTypeToTrack;
  }

  public VWOAdditionalParams setGoalTypeToTrack(GoalEnums.GOAL_TYPES goalTypeToTrack) {
    this.goalTypeToTrack = goalTypeToTrack;
    return this;
  }
}
