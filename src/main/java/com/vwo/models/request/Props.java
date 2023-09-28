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

package com.vwo.models.request;

import com.vwo.models.request.meta.VWOMeta;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Props {
  @JsonProperty("vwo_sdkName")
  private String vwo_sdkName;
  @JsonProperty("vwo_sdkVersion")
  private String vwo_sdkVersion;
  @JsonProperty("vwo_envKey")
  private String vwo_envKey;
  private Integer variation;
  private Integer id;
  private Integer isFirst;
  private VWOMeta vwoMeta;
  private Boolean isCustomEvent;
  @JsonIgnore
  private Map<String,Object> additionalProperties = new HashMap<String,Object>();

  @JsonProperty("vwo_sdkName")

  public String getSdkName() {
    return vwo_sdkName;
  }

  @JsonProperty("vwo_sdkName")
  public Props setSdkName(String sdkName) {
    this.vwo_sdkName = sdkName;
    return this;
  }

  @JsonProperty("vwo_sdkVersion")
  public String getSdkVersion() {
    return vwo_sdkVersion;
  }

  @JsonProperty("vwo_sdkVersion")
  public Props setSdkVersion(String sdkVersion) {
    this.vwo_sdkVersion = sdkVersion;
    return this;
  }

  public Integer getVariation() {
    return variation;
  }

  public Props setVariation(Integer variation) {
    this.variation = variation;
    return this;
  }

  public Integer getIsFirst() {
    return isFirst;
  }

  public Props setIsFirst(Integer isFirst) {
    this.isFirst = isFirst;
    return this;
  }

  public Integer getId() {
    return id;
  }

  public Props setId(Integer id) {
    this.id = id;
    return this;
  }

  public VWOMeta getVwoMeta() {
    return vwoMeta;
  }

  public void setVwoMeta(VWOMeta vwoMeta) {
    this.vwoMeta = vwoMeta;
  }

  public Boolean getIsCustomEvent() {
    return isCustomEvent;
  }

  public void setIsCustomEvent(Boolean isCustomEvent) {
    this.isCustomEvent = isCustomEvent;
  }

  @JsonProperty("vwo_envKey")
  public String getEnvKey() {
    return vwo_envKey;
  }

  @JsonProperty("vwo_envKey")
  public Props setEnvKey(String vwo_envKey) {
    this.vwo_envKey = vwo_envKey;
    return this;
  }

  @JsonAnyGetter
  public Map<String, ?> getAdditionalProperties() {
    return additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperties(Map<String, Object> additionalProperties) {
    this.additionalProperties = additionalProperties;
  }
}
