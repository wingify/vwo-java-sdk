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

package com.vwo.services.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.EventArchEnums;
import com.vwo.enums.HTTPEnums;
import com.vwo.enums.UriEnums;
import com.vwo.logger.Logger;
import com.vwo.logger.LoggerService;
import com.vwo.models.request.EventArchData;
import com.vwo.models.request.EventArchPayload;
import com.vwo.models.request.Props;
import com.vwo.models.request.Event;
import com.vwo.models.request.visitor.Visitor;
import com.vwo.models.request.meta.VWOMeta;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Goal;
import com.vwo.models.response.Settings;
import com.vwo.models.response.Variation;
import com.vwo.services.datalocation.DataLocationManager;
import com.vwo.services.settings.SettingFile;
import com.vwo.utils.UUIDUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.HashSet;

import static com.vwo.utils.HttpUtils.removeNullValues;

public class HttpRequestBuilder {

  public static final String VWO_HOST = UriEnums.BASE_URL.toString();
  public static final String IMPRESSION_PATH = UriEnums.TRACK_USER.toString();
  public static final String GOAL_PATH = UriEnums.TRACK_GOAL.toString();
  public static final String SETTINGS_URL = UriEnums.SETTINGS_URL.toString();
  public static final String WEBHOOK_SETTINGS_URL = UriEnums.WEBHOOK_SETTINGS_URL.toString();
  public static final String PUSH = UriEnums.PUSH.toString();
  public static final String BATCH_EVENTS = UriEnums.BATCH_EVENTS.toString();
  public static final String EVENTS = UriEnums.EVENTS.toString();

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

    // LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.GET_SETTINGS_IMPRESSION_CREATED.value());

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    if (isViaWebhook) {
      return new HttpParams(VWO_HOST, WEBHOOK_SETTINGS_URL, map, HTTPEnums.Verbs.GET);
    } else {
      return new HttpParams(VWO_HOST, SETTINGS_URL, map, HTTPEnums.Verbs.GET);
    }
  }

  public static HttpParams getUserParams(SettingFile settingFile, Campaign campaign, String userId, Variation variation, Map<String, Integer> usageStats) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams = BuildQueryParams.Builder.getInstance()
            .withAccountId(settings.getAccountId())
            .withCampaignId(campaign.getId())
            .withRandom(Math.random())
            .withAp()
            .withEd()
            .withUuid(settings.getAccountId(), userId)
            .withSid(Instant.now().getEpochSecond())
            .withVariation(variation.getId())
            .withsdk()
            .withsdkVersion()
            .withUsageStats(usageStats)
            .withEnvironment(settings.getSdkKey())
            .build();

    Map<String, Object> map = requestParams.convertToMap();

    Map<String, Object> loggingMap = map;
    loggingMap.remove("env");


    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_TRACK_USER"), new HashMap<String, String>() {
      {
        put("properties", requestParams.removeNullValues(loggingMap).toString());
      }
    }));

    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, DataLocationManager.getInstance().getDataLocation(IMPRESSION_PATH), map, HTTPEnums.Verbs.GET);
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

    Map<String, Object> map = requestParams.convertToMap();
    map = requestParams.removeNullValues(map);
    Map<String, Object> finalMap = map;
    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_TRACK_USER"), new HashMap<String, String>() {
      {
        put("properties", finalMap.toString());
      }
    }));

    return map;
  }

  public static HttpParams getGoalParams(SettingFile settingFile, Campaign campaign, String userId, Goal goal, Variation variation, Object revenueValue) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withAccountId(settings.getAccountId())
                    .withCampaignId(campaign.getId())
                    .withRandom(Math.random())
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

    Map<String, Object> map = requestParams.convertToMap();
    Map<String, Object> loggingMap = map;
    loggingMap.remove("env");

    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_TRACK_GOAL"), new HashMap<String, String>() {
      {
        put("properties", requestParams.removeNullValues(loggingMap).toString());
      }
    }));

    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, DataLocationManager.getInstance().getDataLocation(GOAL_PATH), map, HTTPEnums.Verbs.GET);
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

    Map<String, Object> map = requestParams.convertToMap();
    map = requestParams.removeNullValues(map);

    Map<String, Object> finalMap = map;
    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_TRACK_GOAL"), new HashMap<String, String>() {
      {
        put("properties", finalMap.toString());
      }
    }));

    return map;
  }

  public static HttpParams getCustomDimensionParams(SettingFile settingFile, String tagKey, String tagValue, String userId) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withAccountId(settings.getAccountId())
                    .withUuid(settings.getAccountId(), userId)
                    .withTags(tagKey, tagValue)
                    .withSid(Instant.now().getEpochSecond())
                    .withRandom(Math.random())
                    .withAp()
                    .withsdk()
                    .withsdkVersion()
                    .withEnvironment(settings.getSdkKey())
                    .build();
    Map<String, Object> map = requestParams.convertToMap();
    Map<String, Object> loggingMap = map;
    loggingMap.remove("env");

    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_PUSH"), new HashMap<String, String>() {
      {
        put("properties", requestParams.removeNullValues(loggingMap).toString());
      }
    }));

    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new HttpParams(VWO_HOST, DataLocationManager.getInstance().getDataLocation(PUSH), map, HTTPEnums.Verbs.GET);
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

    Map<String, Object> map = requestParams.convertToMap();
    map = requestParams.removeNullValues(map);
    Map<String, Object> finalMap = map;
    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_PUSH"), new HashMap<String, String>() {
      {
        put("properties", finalMap.toString());
      }
    }));

    return map;
  }

  public static HttpParams getBatchEventPostCallParams(String accountId, String apiKey, Queue<Map<String, Object>> properties, Map<String, Integer> usageStats) throws JsonProcessingException {
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withMinifiedSDKVersion()
                    .withMinifiedSDKName()
                    .withSettingsAccountId(accountId)
                    .withEnvironment(apiKey)
                    .withUsageStats(usageStats)
                    .build();

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    HttpParams httpParams = new HttpParams(VWO_HOST, DataLocationManager.getInstance().getDataLocation(BATCH_EVENTS), map, HTTPEnums.Verbs.POST);
    final Header[] headers = new Header[1];

    headers[0] = new BasicHeader("Authorization", apiKey);
    httpParams.setHeaders(headers);

    JsonNode node = objectMapper.createObjectNode().set("ev", objectMapper.valueToTree(properties));
    httpParams.setBody(objectMapper.writeValueAsString(node));

    return httpParams;
  }

  public static HttpParams getEventArchQueryParams(SettingFile settingFile, String eventName, Map<String, Object> properties, Map<String, Integer> usageStats) throws JsonProcessingException {
    Settings settings = settingFile.getSettings();
    BuildQueryParams.Builder requestBuilder =
            BuildQueryParams.Builder.getInstance()
                    .withSettingsAccountId(String.valueOf(settings.getAccountId()))
                    .withEventName(eventName)
                    .withETime(Instant.now().toEpochMilli())
                    .withEnvironment(settings.getSdkKey())
                    .withsdk()
                    .withP("FS")
                    .withRandom(Math.random());

    if (usageStats != null) {
      requestBuilder.withUsageStats(usageStats);
    }

    BuildQueryParams requestParams = requestBuilder.build();

    Map<String, Object> map = requestParams.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


    HttpParams httpParams = new HttpParams(VWO_HOST, DataLocationManager.getInstance().getDataLocation(EVENTS), map, HTTPEnums.Verbs.POST);
    final Header[] headers = new Header[1];
    headers[0] = new BasicHeader("User-Agent", "java");
    httpParams.setHeaders(headers);

    JsonNode node = objectMapper.valueToTree(properties);
    httpParams.setBody(objectMapper.writeValueAsString(node));

    return httpParams;
  }

  public static EventArchPayload getBaseEventArchPayload(SettingFile settingFile, String userId, String eventName) {
    Settings settings = settingFile.getSettings();
    //create props map
    Props props =
        new Props()
          .setSdkName("java")
          .setSdkVersion(UriEnums.SDK_VERSION.toString())
          .setVisitor(new Visitor().setProps(new HashMap<String, Object>() {
              {
                put("vwo_fs_environment", settings.getSdkKey());
              }
            }));

    //create the event map
    Event event = new Event();
    event.setProps(props);
    event.setTime(Instant.now().toEpochMilli());
    event.setName(eventName);

    //create the d map
    EventArchData eventArchData = new EventArchData();
    String uuid = UUIDUtils.getUUId(settings.getAccountId(), userId);
    eventArchData.setMsgId(uuid + "-" + Instant.now().getEpochSecond());
    eventArchData.setVisId(uuid);
    eventArchData.setSessionId(Instant.now().getEpochSecond());
    eventArchData.setEvent(event);
    eventArchData.setVisitor(new Visitor().setProps(new HashMap<String, Object>() {
      {
        put("vwo_fs_environment", settings.getSdkKey());
      }
    }));
    EventArchPayload eventArchPayload = new EventArchPayload();
    eventArchPayload.setD(eventArchData);
    return eventArchPayload;
  }

  public static Map<String, Object> getEventArchTrackUserPayload(SettingFile settingFile, String userId, int campaignId, int variationId) {

    EventArchPayload eventArchPayload =  getBaseEventArchPayload(settingFile, userId, EventArchEnums.VWO_VARIATION_SHOWN.toString());
    eventArchPayload.getD().getEvent().getProps().setVariation(variationId).setIsFirst(1).setId(campaignId);

    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_EVENT_ARCH_TRACK_USER"), new HashMap<String, String>() {
      {
        put("userId", userId);
        put("accountId", String.valueOf(settingFile.getSettings().getAccountId()));
        put("campaignId", String.valueOf(campaignId));
      }
    }));

    Map<String, Object> payloadMap = objectMapper.convertValue(eventArchPayload, Map.class);
    Map<String, Object> event = (Map<String, Object>)  ((Map<String, Object>) payloadMap.get("d")).get("event");
    //    if (usageStats != null) {
    //      HttpUtils.attachUsageStats((Map<String, Object>) event.get("props"), usageStats);
    //    }
    event.put("props", removeNullValues((Map<String, Object>) event.get("props")));

    return payloadMap;
  }

  public static Map<String, Object> getEventArchTrackGoalPayload(SettingFile settingFile, String userId,
                                                                 Map<String, Integer> metricMap, String goalIdentifier, Object revenue, HashSet<String> revenueListProp,Map<String,?> eventProperties) {

    EventArchPayload eventArchPayload =  getBaseEventArchPayload(settingFile, userId, goalIdentifier);
    Map<String, Object> metric = new HashMap<String, Object>();
    Map<String,Object> eventMap = new HashMap<String,Object>();
    ArrayList<String> campaignList = new ArrayList<>();
    for (Map.Entry<String, Integer> query : metricMap.entrySet()) {
      metric.put("id_" + query.getKey(), new ArrayList<String>() {
          {
            add("g_" + query.getValue());
          }
        }
      );
      campaignList.add(query.getKey());
    }
    for (Map.Entry<String, ?> query : eventProperties.entrySet()) {
      eventMap.put(query.getKey(),query.getValue());
    }
    eventArchPayload.getD().getEvent().getProps().setAdditionalProperties(eventMap);
    eventArchPayload.getD().getEvent().getProps().setVwoMeta(new VWOMeta().setMetric(metric));
    eventArchPayload.getD().getEvent().getProps().setCustomEvent(true);

    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_EVENT_ARCH_TRACK_GOAL"), new HashMap<String, String>() {
      {
        put("userId", userId);
        put("accountId", String.valueOf(settingFile.getSettings().getAccountId()));
        put("campaignId", String.join(", ", campaignList));
        put("goalName", goalIdentifier);
      }
    }));

    Map<String, Object> payloadMap = objectMapper.convertValue(eventArchPayload, Map.class);
    Map<String, Object> event = (Map<String, Object>)  ((Map<String, Object>) payloadMap.get("d")).get("event");
    event.put("props", removeNullValues((Map<String, Object>) event.get("props")));
    if (revenue != null && revenueListProp != null && revenueListProp.size() > 0) {
      Map<String, Object> vwoMeta = (Map<String, Object>) ((Map<String, Object>) event.get("props")).get("vwoMeta");
      for (String revenueProp: revenueListProp) {
        vwoMeta.put(revenueProp, revenue);
      }
      ((Map<String, Object>) event.get("props")).put("vwoMeta", vwoMeta);
    }

    return payloadMap;
  }


  public static Map<String, Object> getEventArchPushPayload(SettingFile settingFile, String userId, Map<String, String> customDimensionMap) {

    EventArchPayload eventArchPayload =  getBaseEventArchPayload(settingFile, userId, EventArchEnums.VWO_SYN_VISITOR_PROP.toString());

    for (Map.Entry<String, ?> query : customDimensionMap.entrySet()) {
      eventArchPayload.getD().getVisitor().getProps().put(query.getKey(), query.getValue());
      eventArchPayload.getD().getEvent().getProps().getVisitor().getProps().put(query.getKey(), query.getValue());
    }


    eventArchPayload.getD().getEvent().getProps().setCustomEvent(true);

    LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("IMPRESSION_FOR_EVENT_ARCH_PUSH"), new HashMap<String, String>() {
      {
        put("userId", userId);
        put("accountId", String.valueOf(settingFile.getSettings().getAccountId()));
        put("property", String.valueOf(customDimensionMap));
      }
    }));

    Map<String, Object> payloadMap = objectMapper.convertValue(eventArchPayload, Map.class);
    Map<String, Object> event = (Map<String, Object>)  ((Map<String, Object>) payloadMap.get("d")).get("event");
    event.put("props", removeNullValues((Map<String, Object>) event.get("props")));

    return payloadMap;
  }



  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  private static class BuildQueryParams {
    private String a;
    private String i;
    private String platform;
    private Integer account_id;
    private Integer experiment_id;
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
    private Map<String, Integer> usageStats;
    private String en;
    private Long eTime;
    private String p;
    private static final Logger LOGGER = Logger.getLogger(BuildQueryParams.class);


    private BuildQueryParams(Builder builder) {
      this.a = builder.a;
      this.i = builder.i;
      this.platform = builder.platform;
      this.account_id = builder.account_id;
      this.experiment_id = builder.campaignId;
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
      this.usageStats = builder.usageStats;
      this.en = builder.en;
      this.eTime = builder.eTime;
      this.p = builder.p;
    }


    public static class Builder {
      private String a;
      private String i;
      private String platform;
      private Integer account_id;
      private Integer campaignId;
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
      private Map<String, Integer> usageStats;
      private String en;
      private Long eTime;
      private String p;

      private Builder() {
      }

      public Builder withP(String p) {
        this.p = p;
        return this;
      }

      public Builder withEventName(String eventName) {
        this.en = eventName;
        return this;
      }

      public Builder withETime(Long eTime) {
        this.eTime = eTime;
        return this;
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

      public Builder withUuid(Integer account_id, String uId) {
        this.u = UUIDUtils.getUUId(account_id, uId);
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
          // LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
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

      public Builder withUsageStats(Map<String, Integer> usageStats) {
        this.usageStats = usageStats;
        if (!this.usageStats.isEmpty()) {
          this.usageStats.put("_l", 1);
        }
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

      try {
        // Rename 'sdk_v' as 'sdk-v'
        map.put("sdk-v", map.get("sdk_v"));
        map.remove("sdk_v");
        Map<String, Integer> usageStatsEntry = new ObjectMapper().convertValue(map.get("usageStats"), Map.class);
        if (usageStatsEntry != null && !usageStatsEntry.isEmpty()) {
          for (Map.Entry<String, Integer> mapElement : usageStatsEntry.entrySet()) {
            map.put(mapElement.getKey(), mapElement.getValue());
          }
        }
        map.remove("usageStats");
      } catch (Exception e) {
        e.printStackTrace();
      }

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
