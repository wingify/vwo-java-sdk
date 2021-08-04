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

package com.vwo.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        "isBucketingSeedEnabled"
})
public class Campaign {

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

  public boolean isBucketingSeedEnabled() {
    return isBucketingSeedEnabled;
  }

  public void setBucketingSeedEnabled(boolean bucketingSeedEnabled) {
    isBucketingSeedEnabled = bucketingSeedEnabled;
  }
}
