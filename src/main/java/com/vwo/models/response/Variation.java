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

package com.vwo.models.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.vwo.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "changes",
        "weight",
        "isFeatureEnabled",
        "segments"
})
public class Variation implements Cloneable {

  private static final Logger LOGGER = Logger.getLogger(Variation.class);

  @JsonProperty("id")
  private Integer id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("changes")
  private Changes changes;
  @JsonProperty("weight")
  private double weight;
  @JsonProperty("isFeatureEnabled")
  private boolean isFeatureEnabled;
  @JsonProperty("variables")
  private List<Variable> variables;
  @JsonProperty("segments")
  private Object segments;


  private Integer startRangeVariation;
  private Integer endRangeVariation;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("changes")
  public Changes getChanges() {
    return changes;
  }

  @JsonProperty("changes")
  public void setChanges(Changes changes) {
    this.changes = changes;
  }

  @JsonProperty("weight")
  public double getWeight() {
    return weight;
  }

  @JsonProperty("weight")
  public void setWeight(double weight) {
    this.weight = weight;
  }

  @JsonProperty("isFeatureEnabled")
  public boolean getIsFeatureEnabled() {
    return isFeatureEnabled;
  }

  @JsonProperty("isFeatureEnabled")
  public void setIsFeatureEnabled(boolean isFeatureEnabled) {
    this.isFeatureEnabled = isFeatureEnabled;
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

  public void setSegments(Object segments) {
    this.segments = segments;
  }

  public Integer getStartRangeVariation() {
    return startRangeVariation;
  }

  public void setStartRangeVariation(Integer startRangeVariation) {
    this.startRangeVariation = startRangeVariation;
  }

  public Integer getEndRangeVariation() {
    return endRangeVariation;
  }

  public void setEndRangeVariation(Integer endRangeVariation) {
    this.endRangeVariation = endRangeVariation;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public Variation clone() {
    try {
      return (Variation) super.clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.error("Exception occurred while cloning variation", e.getStackTrace());
      return this;
    }
  }
}
