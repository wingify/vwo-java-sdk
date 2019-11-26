/**
 * Copyright 2019 Wingify Software Pvt. Ltd.
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
        "sdkKey",
        "campaigns",
        "accountId",
        "version"
})
public class Settings {

  @JsonProperty("sdkKey")
  private String sdkKey;
  @JsonProperty("campaigns")
  private List<Campaign> campaigns = null;
  @JsonProperty("accountId")
  private Integer accountId;
  @JsonProperty("version")
  private Integer version;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("sdkKey")
  public String getSdkKey() {
    return sdkKey;
  }

  @JsonProperty("sdkKey")
  public void setSdkKey(String sdkKey) {
    this.sdkKey = sdkKey;
  }

  @JsonProperty("campaigns")
  public List<Campaign> getCampaigns() {
    return campaigns;
  }

  @JsonProperty("campaigns")
  public void setCampaigns(List<Campaign> campaigns) {
    this.campaigns = campaigns;
  }

  @JsonProperty("accountId")
  public Integer getAccountId() {
    return accountId;
  }

  @JsonProperty("accountId")
  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  @JsonProperty("version")
  public Integer getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(Integer version) {
    this.version = version;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}