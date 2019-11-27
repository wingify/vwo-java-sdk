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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpRequestBuilder {

  public static final String VWO_HOST = UriEnums.BASE_URL.toString();
  public static final String IMPRESSION_PATH = UriEnums.TRACK_USER.toString();
  public static final String GOAL_PATH = UriEnums.TRACK_GOAL.toString();

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger LOGGER = Logger.getLogger(HttpRequestBuilder.class);

  public static HttpParams getUserParams(SettingFile settingFile, Campaign campaign, String userId, Variation variation) {
    Settings settings = settingFile.getSettings();
    BuildQueryParams requestParams =
            BuildQueryParams.Builder.getInstance()
                    .withaccount_id(settings.getAccountId())
                    .withexperiment_id(campaign.getId())
                    .withRandom(Math.random())
                    .withAp()
                    .withEd()
                    .withuId(userId)
                    .withUuid(settings.getAccountId(), userId)
                    .withsId(Instant.now().getEpochSecond())
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
                    .withaccount_id(settings.getAccountId())
                    .withexperiment_id(campaign.getId())
                    .withRandom(Math.random())
                    .withuId(userId)
                    .withAp()
                    .withUuid(settings.getAccountId(), userId)
                    .withGoalId(goal.getId())
                    .withsId(Instant.now().getEpochSecond())
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

  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  private static class BuildQueryParams {
    private Integer account_id;
    private Integer experiment_id;
    private String uId;
    private String u;
    private Integer combination;
    private Double random;
    private Integer goal_id;
    private Object r;
    private long sId;
    private String ap;
    private String ed;
    private String sdk;
    private String sdk_v;
    private static final Logger LOGGER = Logger.getLogger(BuildQueryParams.class);


    private BuildQueryParams(Builder builder) {
      this.account_id = builder.account_id;
      this.experiment_id = builder.experiment_id;
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
    }


    public static class Builder {
      private Integer account_id;
      private Integer experiment_id;
      private String uId;
      private String u;
      private Integer combination;
      private Double random;
      private Integer goal_id;
      private Object r;
      private long sId;
      private final UUID CONSTANT_NAMESPACE = UUIDUtils.nameUUIDFromNamespaceAndString(UUIDUtils.NAMESPACE_URL, "https://vwo.com");
      private String ap;
      private String ed;
      private String sdk;
      private String sdk_v;

      private Builder() {
      }

      public Builder withaccount_id(Integer account_id) {
        this.account_id = account_id;
        return this;
      }

      public Builder withexperiment_id(Integer experiment_id) {
        this.experiment_id = experiment_id;
        return this;
      }

      public Builder withuId(String uId) {
        this.uId = uId;
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

      public Builder withsId(long sId) {
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
        this.sdk_v = "1.4.0";
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
              + "account_id=" + this.account_id
              + ", experiment_id=" + this.experiment_id
              + ", uId='" + this.uId + '\''
              + ", u='" + this.u + '\''
              + ", combination=" + this.combination
              + ", random=" + this.random
              + ", sId=" + this.sId
              + ", ap='" + this.ap + '\''
              + ", sdk=" + this.sdk
              + ", sdk-v=" + this.sdk_v;

      if (this.goal_id != null) {
        request += ", goal_id='" + this.goal_id + '\'';

        if (this.r != null) {
          request += ", r='" + this.r + '\'';
        }
      } else {
        request += ", ed='" + this.ed + '\'';
      }

      return request + '}';
    }
  }
}
