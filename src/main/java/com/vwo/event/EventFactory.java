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
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vwo.URIConstants;
import com.vwo.config.ProjectConfig;
import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.logger.LoggerManager;
import com.vwo.models.Campaign;
import com.vwo.models.Goal;
import com.vwo.models.SettingFileConfig;
import com.vwo.models.Variation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class EventFactory {

  public static final String VWO_HOST = URIConstants.BASE_URL.toString();
  public static final String IMPRESSION_PATH = URIConstants.TRACK_USER.toString();
  public static final String GOAL_PATH = URIConstants.TRACK_GOAL.toString();

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final LoggerManager LOGGER = LoggerManager.getLogger(EventFactory.class);

  public static DispatchEvent createImpressionLogEvent(ProjectConfig projectConfig, Campaign campaignTestKey, String userId, Variation variation) {
    SettingFileConfig settingFileConfig = projectConfig.getSettingFileConfig();
    Event impressionEvent =
            Event.Builder.getInstance()
                    .withaccount_id(settingFileConfig.getAccountId())
                    .withexperiment_id(campaignTestKey.getId())
                    .withRandom(Math.random())
                    .withAp()
                    .withEd()
                    .withuId(userId)
                    .withUuid(settingFileConfig.getAccountId(), userId)
                    .withsId(Instant.now().getEpochSecond())
                    .withVariation(variation.getId())
                    .withsdk()
                    .withsdkVersion()
                    .build();

    LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.TRACK_USER_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("impressionEvent", impressionEvent.toString());
      }
    }));

    Map<String, Object> map = impressionEvent.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new DispatchEvent(VWO_HOST, IMPRESSION_PATH, map, DispatchEvent.RequestMethod.GET, null);
  }

  public static DispatchEvent createGoalLogEvent(ProjectConfig projectConfig, Campaign campaignTestKey, String userId, Goal goal, Variation variation, Object revenueValue) {
    SettingFileConfig settingFileConfig = projectConfig.getSettingFileConfig();
    Event goalEvent =
            Event.Builder.getInstance()
                    .withaccount_id(settingFileConfig.getAccountId())
                    .withexperiment_id(campaignTestKey.getId())
                    .withRandom(Math.random())
                    .withuId(userId)
                    .withAp()
                    .withUuid(settingFileConfig.getAccountId(), userId)
                    .withGoalId(goal.getId())
                    .withsId(Instant.now().getEpochSecond())
                    .withRevenue(revenueValue)
                    .withVariation(variation.getId())
                    .withsdk()
                    .withsdkVersion()
                    .build();

    LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.TRACK_GOAL_IMPRESSION_CREATED.value(new HashMap<String, String>() {
      {
        put("userId", userId);
        put("goalEvent", goalEvent.toString());
      }
    }));

    Map<String, Object> map = goalEvent.convertToMap();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return new DispatchEvent(VWO_HOST, GOAL_PATH, map, DispatchEvent.RequestMethod.GET, null);
  }
}
