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

package com.vwo.services.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.HTTPEnums;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.enums.UriEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Goal;
import com.vwo.models.Settings;
import com.vwo.models.Variation;
import com.vwo.services.batch.FlushInterface;
import com.vwo.services.settings.SettingFile;
import com.vwo.utils.UUIDUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class HttpRequestBuilder {

  public static final String VWO_HOST = UriEnums.BASE_URL.toString();
  public static final String IMPRESSION_PATH = UriEnums.TRACK_USER.toString();
  public static final String GOAL_PATH = UriEnums.TRACK_GOAL.toString();
  public static final String SETTINGS_URL = UriEnums.SETTINGS_URL.toString();
  public static final String WEBHOOK_SETTINGS_URL = UriEnums.WEBHOOK_SETTINGS_URL.toString();
  public static final String PUSH = UriEnums.PUSH.toString();
  public static final String BATCH_EVENTS = UriEnums.BATCH_EVENTS.toString();

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger LOGGER = Logger.getLogger(HttpRequestBuilder.class);

  public static HttpParams getSettingParams(String accountID, String sdkKey, boolean isViaWebhook) {
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withSettingsAccountId(accountID)
                    .withR(Math.random())
                    .withSdkKey(sdkKey)
                    .withsdk()
                    .withsdkVersion()
                    .withPlatform()
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.GET_SETTINGS_IMPRESSION_CREATED.value());

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    if (isViaWebhook) {
      return new HttpParams(VWO_HOST, WEBHOOK_SETTINGS_URL, map, HTTPEnums.Verbs.GET);
    } else {
      return new HttpParams(VWO_HOST, SETTINGS_URL, map, HTTPEnums.Verbs.GET);
    }
  }

  public static HttpParams getUserParams(SettingFile settingFile, Campaign campaign, String userId, Variation variation) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams = BuildQueryParams.Builder.getInstance()
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
            .withEnvironment(settings.getSdkKey())
            .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.TRACK_USER_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, IMPRESSION_PATH, map, HTTPEnums.Verbs.GET);
  }

  public static Map<String, Object> getBatchEventForTrackingUser(SettingFile settingFile, Campaign campaign, String userId, Variation variation) {
    Settings settings = settingFile.getSettings();

    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withMinifiedCampaignId(campaign.getId())
                    .withMinifiedVariationId(variation.getId())
                    .withMinifiedEventType(1)
                    .withSid(Instant.now().getEpochSecond())
                    .withUuid(settings.getAccountId(), userId)
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.TRACK_USER_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
      }
    }));
    Map<String, Object> map = requestParams.convertToMap();
    return requestParams.removeNullValues(map);
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
                    .withEnvironment(settings.getSdkKey())
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.TRACK_GOAL_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, GOAL_PATH, map, HTTPEnums.Verbs.GET);
  }

  public static Map<String, Object> getBatchEventForTrackingGoal(SettingFile settingFile, Campaign campaign, String userId, Goal goal, Variation variation, Object revenueValue) {
    Settings settings = settingFile.getSettings();

    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withMinifiedCampaignId(campaign.getId())
                    .withMinifiedVariationId(variation.getId())
                    .withMinifiedEventType(2)
                    .withMinifiedGoalId(goal.getId())
                    .withRevenue(revenueValue)
                    .withSid(Instant.now().getEpochSecond())
                    .withUuid(settings.getAccountId(), userId)
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.TRACK_GOAL_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    return requestParams.removeNullValues(map);
  }

  public static HttpParams getCustomDimensionParams(SettingFile settingFile, String tagKey, String tagValue, String userId) {
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
                    .withEnvironment(settings.getSdkKey())
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.POST_SEGMENTATION_REQUEST_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, PUSH, map, HTTPEnums.Verbs.GET);
  }

  public static Map<String, Object> getBatchEventForCustomDimension(SettingFile settingFile, String tagKey, String tagValue, String userId) {
    Settings settings = settingFile.getSettings();

    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withMinifiedEventType(3)
                    .withMinifiedTags(tagKey, tagValue)
                    .withSid(Instant.now().getEpochSecond())
                    .withUuid(settings.getAccountId(), userId)
                    .build();

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.POST_SEGMENTATION_REQUEST_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
      }
    }));

    Map<String, Object> map = requestParams.convertToMap();
    return requestParams.removeNullValues(map);
  }

  public static HttpParams getBatchEventPostCallParams(String accountId, String apiKey, Queue<Map<String, Object>> properties, FlushInterface flushCallback) throws JsonProcessingException {
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withMinifiedSDKVersion()
                    .withMinifiedSDKName()
                    .withSettingsAccountId(accountId)
                    .withEnvironment(apiKey)
                    .build();

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    HttpParams httpParams = new HttpParams(VWO_HOST, BATCH_EVENTS, map, HTTPEnums.Verbs.POST);
    httpParams.setFlushCallback(flushCallback);
    final Header[] headers = new Header[1];

    headers[0] = new BasicHeader("Authorization", apiKey);
    httpParams.setHeaders(headers);

    JsonNode node = objectMapper.createObjectNode().set("ev", objectMapper.valueToTree(properties));
    httpParams.setBody(objectMapper.writeValueAsString(node));

    return httpParams;
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
    private String t;
    private String sv;
    private String sd;
    private String env;
    private Integer e;
    private Integer c;
    private Integer eT;
    private Integer g;
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
      this.e = builder.e;
      this.c = builder.c;
      this.eT = builder.eT;
      this.g = builder.g;
      this.t = builder.t;
      this.sv = builder.sv;
      this.sd = builder.sd;
      this.env = builder.env;
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
      private String ap;
      private String ed;
      private String sdk;
      private String sdk_v;
      private String tags;
      private String t;
      private String sv;
      private String sd;
      private String env;
      private Integer e;
      private Integer c;
      private Integer eT;
      private Integer g;

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

      public Builder withMinifiedCampaignId(Integer campaignId) {
        this.e = campaignId;
        return this;
      }

      public Builder withuId(String uId) {
        this.uId = URLEncoder.encode(uId);
        return this;
      }

      public Builder withUuid(Integer account_id, String uId) {
        this.u = UUIDUtils.getUUId(account_id, uId);
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

      public Builder withMinifiedVariationId(Integer variationId) {
        this.c = variationId;
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

      public Builder withMinifiedGoalId(Integer goal_id) {
        this.g = goal_id;
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

      public Builder withMinifiedTags(String tagKey, String tagValue) {
        try {
          this.t = URLEncoder.encode("{\"u\":{\"" + tagKey + "\":\"" + tagValue + "\"}}", StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
          e.printStackTrace();
        }
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
        this.sdk_v = UriEnums.SDK_VERSION.toString();
        return this;
      }

      public Builder withMinifiedSDKVersion() {
        this.sv = UriEnums.SDK_VERSION.toString();
        return this;
      }

      public Builder withMinifiedSDKName() {
        this.sd = UriEnums.SDK_NAME.toString();
        return this;
      }

      public Builder withMinifiedEventType(Integer eventType) {
        this.eT = eventType;
        return this;
      }

      public Builder withEnvironment(String sdkKey) {
        this.env = sdkKey;
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

    public Map<String, Object> removeNullValues(Map<String, Object> urlMap) {
      Map<String, Object> requestMap = new HashMap<String, Object>();
      for (Map.Entry<String, Object> query : urlMap.entrySet()) {
        if (query.getValue() != null) {
          requestMap.put(query.getKey(), query.getValue());
        }
      }
      return requestMap;
    }
  }
}
