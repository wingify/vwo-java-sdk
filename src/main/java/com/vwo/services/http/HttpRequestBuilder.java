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

package com.vwo.services.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vwo.enums.HTTPEnums;
import com.vwo.enums.UriEnums;
import com.vwo.models.Settings;
import com.vwo.services.settings.SettingFile;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Goal;
import com.vwo.models.Variation;
import com.vwo.utils.UUIDUtils;

import java.net.URLEncoder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpRequestBuilder {

  public static final String VWO_HOST = UriEnums.BASE_URL.toString();
  public static final String IMPRESSION_PATH = UriEnums.TRACK_USER.toString();
  public static final String GOAL_PATH = UriEnums.TRACK_GOAL.toString();
  public static final String ACCOUNT_SETTINGS = UriEnums.ACCOUNT_SETTINGS.toString();
  public static final String PUSH = UriEnums.PUSH.toString();

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger LOGGER = Logger.getLogger(HttpRequestBuilder.class);

  public static HttpParams getSettingParams(String accountID, String sdkKey) {
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withSettingsAccountId(accountID)
                    .withR(Math.random())
                    .withSdkKey(sdkKey)
                    .withsdk()
                    .withsdkVersion()
                    .withPlatform()
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.GET_SETTINGS_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("requestParams", requestParams.toString());
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, ACCOUNT_SETTINGS, map, HTTPEnums.Verbs.GET);
  }

  public static HttpParams getUserParams(SettingFile settingFile, Campaign campaign, String userId, Variation variation) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withAccountId(settings.getAccountId())
                    .withCampaignId(campaign.getId())
                    .withRandom(Math.random())
                    .withAp()
                    .withEd()
                    .withuId(userId)
                    .withUuid(settings.getAccountId(), userId)
                    .withSid(Instant.now().getEpochSecond())
                    .withVariation(variation.getId())
                    .withsdk()
                    .withsdkVersion()
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.TRACK_USER_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("requestParams", requestParams.toString());
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, IMPRESSION_PATH, map, HTTPEnums.Verbs.GET);
  }

  public static HttpParams getGoalParams(SettingFile settingFile, Campaign campaign, String userId, Goal goal, Variation variation, Object revenueValue) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withAccountId(settings.getAccountId())
                    .withCampaignId(campaign.getId())
                    .withRandom(Math.random())
                    .withuId(userId)
                    .withAp()
                    .withUuid(settings.getAccountId(), userId)
                    .withGoalId(goal.getId())
                    .withSid(Instant.now().getEpochSecond())
                    .withRevenue(revenueValue)
                    .withVariation(variation.getId())
                    .withsdk()
                    .withsdkVersion()
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.TRACK_GOAL_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("requestParams", requestParams.toString());
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, GOAL_PATH, map, HTTPEnums.Verbs.GET);
  }

  public static HttpParams getPostCustomDimensionParams(SettingFile settingFile, String tagKey, String tagValue, String userId) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withAccountId(settings.getAccountId())
                    .withuId(userId)
                    .withUuid(settings.getAccountId(), userId)
                    .withTags(tagKey, tagValue)
                    .withSid(Instant.now().getEpochSecond())
                    .withRandom(Math.random())
                    .withAp()
                    .withsdk()
                    .withsdkVersion()
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.POST_SEGMENTATION_REQUEST_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("requestParams", requestParams.toString());
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, PUSH, map, HTTPEnums.Verbs.GET);
  }

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  private static class BuildQueryParams {
    private String a;
    private String i;
    private String platform;
    private Integer account_id;
    private Integer experiment_id;
    private String uId;
    private String u;
    private Integer combination;
    private Double random;
    private Integer goal_id;
    private Object r;
    private Long sId;
    private String ap;
    private String ed;
    private String sdk;
    private String sdk_v;
    private String tags;
    private static final Logger LOGGER = Logger.getLogger(BuildQueryParams.class);


    private BuildQueryParams(Builder builder) {
      this.a = builder.a;
      this.i = builder.i;
      this.platform = builder.platform;
      this.account_id = builder.account_id;
      this.experiment_id = builder.campaignId;
      this.uId = builder.uId;
      this.u = builder.u;
      this.combination = builder.combination;
      this.random = builder.random;
      this.goal_id = builder.goal_id;
      this.r = builder.r;
      this.sId = builder.sId;
      this.ap = builder.ap;
      this.ed = builder.ed;
      this.sdk = builder.sdk;
      this.sdk_v = builder.sdk_v;
      this.tags = builder.tags;
    }


    public static class Builder {
      private String a;
      private String i;
      private String platform;
      private Integer account_id;
      private Integer campaignId;
      private String uId;
      private String u;
      private Integer combination;
      private Double random;
      private Integer goal_id;
      private Object r;
      private Long sId;
      private final UUID CONSTANT_NAMESPACE = UUIDUtils.nameUUIDFromNamespaceAndString(UUIDUtils.NAMESPACE_URL, "https://vwo.com");
      private String ap;
      private String ed;
      private String sdk;
      private String sdk_v;
      private String tags;

      private Builder() {
      }

      // Used just for get settings
      public Builder withSettingsAccountId(String account_id) {
        this.a = account_id;
        return this;
      }

      // Used just for get settings
      public Builder withSdkKey(String sdkKey) {
        this.i = sdkKey;
        return this;
      }

      // Used just for get settings. Same as 'random' but required as 'r' on dacdn.
      public Builder withR(Double random) {
        this.r = random;
        return this;
      }

      // Used just for get settings. Same as 'ed' but required as 'platform' on dacdn.
      public Builder withPlatform() {
        this.platform = "server";
        return this;
      }

      public Builder withAccountId(Integer account_id) {
        this.account_id = account_id;
        return this;
      }

      public Builder withCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
        return this;
      }

      public Builder withuId(String uId) {
        this.uId = URLEncoder.encode(uId);
        return this;
      }

      public Builder withUuid(Integer account_id, String uId) {
        UUID accountUuid = UUIDUtils.nameUUIDFromNamespaceAndString(CONSTANT_NAMESPACE, this.account_id.toString());
        UUID userUuid = UUIDUtils.nameUUIDFromNamespaceAndString(accountUuid, this.uId);
        this.u = userUuid.toString().replace("-", "").toUpperCase();
        LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.UUID_GENERATED.value(new HashMap<String, String>() {
          {
            put("userId", uId);
            put("accountId", String.valueOf(account_id));
            put("uuid", u);
          }
        }));
        return this;
      }

      public Builder withVariation(Integer combination) {
        this.combination = combination;
        return this;

      }

      public Builder withRandom(Double random) {
        this.random = random;
        return this;
      }

      public Builder withSid(Long sId) {
        this.sId = sId;
        return this;
      }

      public Builder withGoalId(Integer goal_id) {
        this.goal_id = goal_id;
        return this;
      }

      public Builder withRevenue(Object r) {
        this.r = r;
        return this;
      }

      public Builder withTags(String tagKey, String tagValue) {
        this.tags = "{\"u\":{\"" + tagKey + "\":\"" + tagValue + "\"}}";
        return this;
      }

      public Builder withAp() {
        this.ap = "server";
        return this;
      }

      public Builder withEd() {
        this.ed = "{\"p\":\"server\"}";
        return this;
      }

      public Builder withsdk() {
        this.sdk = "java";
        return this;
      }

      public Builder withsdkVersion() {
        this.sdk_v = "1.8.1";
        return this;
      }

      public static Builder getInstance() {
        return new Builder();
      }

      public BuildQueryParams build() {
        return new BuildQueryParams(this);
      }
    }

    public Map<String, Object> convertToMap() {
      Map<String, Object> map = new ObjectMapper().convertValue(this, Map.class);

      // Rename 'sdk_v' as 'sdk-v'
      map.put("sdk-v", map.get("sdk_v"));
      map.remove("sdk_v");

      return map;
    }

    @Override
    public String toString() {
      String request = "Event{"
              + this.a != null ? "a=" + this.a : ""
              + this.i != null ? "i=" + this.i : ""
              + this.platform != null ? "platform=" + this.platform : ""
              + this.account_id != null ? "account_id=" + this.account_id : ""
              + this.experiment_id != null ? ", experiment_id=" + this.experiment_id : ""
              + this.uId != null ? ", uId='" + this.uId + '\'' : ""
              + this.u != null ? ", u='" + this.u + '\'' : ""
              + this.combination != null ? ", combination=" + this.combination : ""
              + this.random != null ? ", random=" + this.random : ""
              + this.sId != null ? ", sId=" + this.sId : ""
              + this.ap != null ? ", ap='" + this.ap + '\'' : ""
              + ", sdk=" + this.sdk
              + ", sdk-v=" + this.sdk_v
              + this.tags != null ? ", tags=" + this.tags : "";

      if (this.goal_id != null) {
        request += ", goal_id='" + this.goal_id + '\'';

        if (this.r != null) {
          request += ", r='" + this.r + '\'';
        }
      } else if (this.ed != null) {
        request += ", ed='" + this.ed + '\'';
      }

      return request + '}';
    }
  }
}
