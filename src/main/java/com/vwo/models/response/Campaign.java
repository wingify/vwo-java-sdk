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

package com.vwo.models.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// is this causing a crash?
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "goals",
        "variations",
        "id",
        "percentTraffic",
        "key",
        "status",
        "type",
        "variables",
        "segments",
        "name",
        "isForcedVariationEnabled",
        "isBucketingSeedEnabled",
        "isUserListEnabled",
        "isAlwaysCheckSegment",
        "isMAB"
})
public class Campaign implements  Cloneable {

  @JsonProperty("goals")
  private List<Goal> goals = null;
  @JsonProperty("variations")
  private List<Variation> variations = null;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("percentTraffic")
  private Integer percentTraffic;
  @JsonProperty("key")
  private String key;
  @JsonProperty("status")
  private String status;
  @JsonProperty("type")
  private String type;
  @JsonProperty("variables")
  private List<Variable> variables;
  @JsonProperty("segments")
  private Object segments;
  @JsonProperty("name")
  private String name;
  @JsonProperty("isForcedVariationEnabled")
  private boolean isForcedVariationEnabled;
  @JsonProperty("isBucketingSeedEnabled")
  private boolean isBucketingSeedEnabled;
  @JsonProperty("isUserListEnabled")
  private boolean isUserListEnabled;
  @JsonProperty("isAlwaysCheckSegment")
  private boolean isAlwaysCheckSegment;
  @JsonProperty("isOB")
  private boolean isOB;
  @JsonProperty("isOBv2")
  private boolean isOBv2;
  @JsonProperty("isMAB")
  private boolean isMAB;

  private Integer startRange;
  private Integer endRange;
  private double weight;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("goals")
  public List<Goal> getGoals() {
    return goals;
  }

  @JsonProperty("goals")
  public void setGoals(List<Goal> goals) {
    this.goals = goals;
  }

  @JsonProperty("variations")
  public List<Variation> getVariations() {
    return variations;
  }

  @JsonProperty("variations")
  public void setVariations(List<Variation> variations) {
    this.variations = variations;
  }

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("percentTraffic")
  public Integer getPercentTraffic() {
    return percentTraffic;
  }

  @JsonProperty("percentTraffic")
  public void setPercentTraffic(Integer percentTraffic) {
    this.percentTraffic = percentTraffic;
  }

  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  @JsonProperty("key")
  public void setKey(String key) {
    this.key = key;
  }

  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("variables")
  public List<Variable> getVariables() {
    return variables;
  }

  @JsonProperty("variables")
  public void setVariables(List<Variable> variables) {
    this.variables = variables;
  }

  @JsonProperty("segments")
  public Object getSegments() {
    return segments;
  }

  @JsonProperty("segments")
  public void setSegments(Object segments) {
    this.segments = segments;
  }

  @JsonProperty("isForcedVariationEnabled")
  public boolean getIsForcedVariationEnabled() {
    return isForcedVariationEnabled;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public Campaign clone() {
    try {
      return (Campaign) super.clone();
    } catch (CloneNotSupportedException e) {
      return this;
    }
  }

  public Integer getStartRange() {
    return startRange;
  }

  public void setStartRange(Integer startRange) {
    this.startRange = startRange;
  }

  public Integer getEndRange() {
    return endRange;
  }

  public void setEndRange(Integer endRange) {
    this.endRange = endRange;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public boolean isBucketingSeedEnabled() {
    return isBucketingSeedEnabled;
  }

  public void setBucketingSeedEnabled(boolean bucketingSeedEnabled) {
    isBucketingSeedEnabled = bucketingSeedEnabled;
  }

  public boolean isUserListEnabled() {
    return isUserListEnabled;
  }

  public void setUserListEnabled(boolean userListEnabled) {
    isUserListEnabled = userListEnabled;
  }

  public boolean getIsAlwaysCheckSegment() {
    return isAlwaysCheckSegment;
  }
  
  @JsonProperty("isOB")
  public boolean getIsOB() {
    return isOB;
  }
  
  @JsonProperty("isOB")
  public void setIsOB(boolean isOB) {
    this.isOB = isOB;
  }
  
  @JsonProperty("isMAB")
  public boolean getIsMAB() {
    return isMAB;
  }
  
  @JsonProperty("isMAB")
  public void setIsMAB(boolean isMAB) {
    this.isMAB = isMAB;
  }
  
  @JsonProperty("isOBv2")
  public boolean getIsOBv2() {
    return isOBv2;
  }
  
  @JsonProperty("isOBv2")
  public void setIsOBv2(boolean isOBv2) {
    this.isOBv2 = isOBv2;
  }
}
