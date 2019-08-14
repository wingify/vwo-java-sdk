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
public class SettingFileConfig {

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