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
import com.vwo.models.request.visitor.Visitor;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class Props {

  private Visitor $visitor;
  private String sdkName;
  private String sdkVersion;
  private Integer variation;
  private Integer id;
  private Integer isFirst;
  private VWOMeta vwoMeta;
  private Boolean isCustomEvent;
  @JsonIgnore
  private Map<String,Object> additionalProperties = new HashMap<String,Object>();

  public Visitor getVisitor() {
    return $visitor;
  }

  public Props setVisitor(Visitor $visitor) {
    this.$visitor = $visitor;
    return this;
  }

  public String getSdkName() {
    return sdkName;
  }

  public Props setSdkName(String sdkName) {
    this.sdkName = sdkName;
    return this;
  }

  public String getSdkVersion() {
    return sdkVersion;
  }

  public Props setSdkVersion(String sdkVersion) {
    this.sdkVersion = sdkVersion;
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

  public Boolean getCustomEvent() {
    return isCustomEvent;
  }

  public void setCustomEvent(Boolean customEvent) {
    isCustomEvent = customEvent;
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
