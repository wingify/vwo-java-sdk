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

package com.vwo.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.UUIDType5;
import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.logger.LoggerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Event {
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
  private static final LoggerManager LOGGER = LoggerManager.getLogger(Event.class);


  private Event(Builder builder) {
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
    private final UUID CONSTANT_NAMESPACE = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_URL, "https://vwo.com");
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
      UUID accountUuid = UUIDType5.nameUUIDFromNamespaceAndString(CONSTANT_NAMESPACE, this.account_id.toString());
      UUID userUuid = UUIDType5.nameUUIDFromNamespaceAndString(accountUuid, this.uId);
      this.u = userUuid.toString().replace("-", "").toUpperCase();
      LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.UUID_GENERATED.value(new HashMap<String, String>() {
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
      this.sdk_v = "1.3.0";
      return this;
    }

    public static Builder getInstance() {
      return new Builder();
    }

    public Event build() {
      return new Event(this);
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
    String event = "Event{"
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
      event += ", goal_id='" + this.goal_id + '\'';

      if (this.r != null) {
        event += ", r='" + this.r + '\'';
      }
    } else {
      event += ", ed='" + this.ed + '\'';
    }

    return event + '}';
  }
}
