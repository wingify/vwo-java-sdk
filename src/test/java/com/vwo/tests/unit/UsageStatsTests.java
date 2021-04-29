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

package com.vwo.tests.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.vwo.VWO;
import com.vwo.enums.GoalEnums;
import com.vwo.enums.VWOEnums;
import com.vwo.logger.VWOLogger;
import com.vwo.models.BatchEventData;
import com.vwo.services.batch.FlushInterface;
import com.vwo.services.integrations.IntegrationEventListener;
import com.vwo.services.storage.Storage;
import com.vwo.tests.data.Settings;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UsageStatsTests {

  @Test
  public void withoutConfig() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50).build();

    assertTrue(vwo.getUsageStats().isEmpty());
  }

  @Test
  public void withStorageServiceOnly() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50).withUserStorage(getUSerStorage()).build();
    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertNull(vwo.getUsageStats().get("is_i"));
    assertNull(vwo.getUsageStats().get("is_eb"));
    assertNull(vwo.getUsageStats().get("is_cl"));
    assertNull(vwo.getUsageStats().get("is_ll"));
    assertNull(vwo.getUsageStats().get("gt"));
    assertNull(vwo.getUsageStats().get("tru"));
    assertNull(vwo.getUsageStats().get("poll"));
  }

  @Test
  public void alongWithCustomLogger() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50).withUserStorage(getUSerStorage()).withCustomLogger(getCustomLogger()).build();
    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertEquals(vwo.getUsageStats().get("is_cl"), 1);
    assertEquals(vwo.getUsageStats().get("is_ll"), 1);
    assertNull(vwo.getUsageStats().get("is_i"));
    assertNull(vwo.getUsageStats().get("is_eb"));
    assertNull(vwo.getUsageStats().get("gt"));
    assertNull(vwo.getUsageStats().get("tru"));
    assertNull(vwo.getUsageStats().get("poll"));
  }

  @Test
  public void alongWithGoalTypeToTrack() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50)
            .withUserStorage(getUSerStorage())
            .withCustomLogger(getCustomLogger())
            .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
            .build();
    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertEquals(vwo.getUsageStats().get("is_cl"), 1);
    assertEquals(vwo.getUsageStats().get("is_ll"), 1);
    assertEquals(vwo.getUsageStats().get("gt"), 1);
    assertNull(vwo.getUsageStats().get("is_i"));
    assertNull(vwo.getUsageStats().get("is_eb"));
    assertNull(vwo.getUsageStats().get("tru"));
    assertNull(vwo.getUsageStats().get("poll"));
  }

  @Test
  public void alongWithPolling() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50)
            .withUserStorage(getUSerStorage())
            .withCustomLogger(getCustomLogger())
            .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
            .withPollingInterval(20)
            .withSdkKey("sdk_key")
            .build();
    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertEquals(vwo.getUsageStats().get("is_cl"), 1);
    assertEquals(vwo.getUsageStats().get("is_ll"), 1);
    assertEquals(vwo.getUsageStats().get("gt"), 1);
    assertEquals(vwo.getUsageStats().get("poll"), 1);
    assertNull(vwo.getUsageStats().get("is_i"));
    assertNull(vwo.getUsageStats().get("is_eb"));
    assertNull(vwo.getUsageStats().get("tru"));
  }

  @Test
  public void alongWithIntegrations() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50)
            .withUserStorage(getUSerStorage())
            .withCustomLogger(getCustomLogger())
            .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
            .withPollingInterval(20)
            .withSdkKey("sdk_key")
            .withIntegrations(getEventData())
            .build();

    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertEquals(vwo.getUsageStats().get("is_cl"), 1);
    assertEquals(vwo.getUsageStats().get("is_ll"), 1);
    assertEquals(vwo.getUsageStats().get("gt"), 1);
    assertEquals(vwo.getUsageStats().get("poll"), 1);
    assertEquals(vwo.getUsageStats().get("is_i"), 1);
    assertNull(vwo.getUsageStats().get("is_eb"));
    assertNull(vwo.getUsageStats().get("tru"));
  }

  @Test
  public void alongWithShouldTrackReturningUser() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50)
            .withUserStorage(getUSerStorage())
            .withCustomLogger(getCustomLogger())
            .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
            .withPollingInterval(20)
            .withSdkKey("sdk_key")
            .withShouldTrackReturningUser(true)
            .withIntegrations(getEventData())
            .build();

    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertEquals(vwo.getUsageStats().get("is_cl"), 1);
    assertEquals(vwo.getUsageStats().get("is_ll"), 1);
    assertEquals(vwo.getUsageStats().get("gt"), 1);
    assertEquals(vwo.getUsageStats().get("poll"), 1);
    assertEquals(vwo.getUsageStats().get("is_i"), 1);
    assertEquals(vwo.getUsageStats().get("tru"), 1);
    assertNull(vwo.getUsageStats().get("is_eb"));
  }

  @Test
  public void alongWithEventBatching() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50)
            .withUserStorage(getUSerStorage())
            .withCustomLogger(getCustomLogger())
            .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
            .withPollingInterval(20)
            .withSdkKey("sdk_key")
            .withShouldTrackReturningUser(true)
            .withIntegrations(getEventData())
            .withBatchEvents(getBatchingData())
            .build();

    assertFalse(vwo.getUsageStats().isEmpty());
    assertEquals(vwo.getUsageStats().get("is_ss"), 1);
    assertEquals(vwo.getUsageStats().get("is_cl"), 1);
    assertEquals(vwo.getUsageStats().get("is_ll"), 1);
    assertEquals(vwo.getUsageStats().get("gt"), 1);
    assertEquals(vwo.getUsageStats().get("poll"), 1);
    assertEquals(vwo.getUsageStats().get("is_i"), 1);
    assertEquals(vwo.getUsageStats().get("tru"), 1);
    assertEquals(vwo.getUsageStats().get("is_eb"), 1);
  }

  @Test
  public void alongWithDevelopmentMode() {
    VWO vwo = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50)
            .withUserStorage(getUSerStorage())
            .withCustomLogger(getCustomLogger())
            .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
            .withPollingInterval(20)
            .withSdkKey("sdk_key")
            .withShouldTrackReturningUser(true)
            .withIntegrations(getEventData())
            .withBatchEvents(getBatchingData())
            .withDevelopmentMode(true)
            .build();

    assertTrue(vwo.getUsageStats().isEmpty());
    assertNull(vwo.getUsageStats().get("is_ss"));
    assertNull(vwo.getUsageStats().get("is_cl"));
    assertNull(vwo.getUsageStats().get("is_ll"));
    assertNull(vwo.getUsageStats().get("gt"));
    assertNull(vwo.getUsageStats().get("poll"));
    assertNull(vwo.getUsageStats().get("is_i"));
    assertNull(vwo.getUsageStats().get("tru"));
    assertNull(vwo.getUsageStats().get("is_eb"));
  }

  public static BatchEventData getBatchingData() {
    BatchEventData batchData = new BatchEventData();
    batchData.setEventsPerRequest(2);
    batchData.setRequestTimeInterval(20);
    batchData.setFlushCallback(new FlushInterface() {
      @Override
      public void onFlush(String s, JsonNode objectNode) {
      }
    });
    return batchData;
  }

  private IntegrationEventListener getEventData() {
    return new IntegrationEventListener() {
      @Override
      public void onEvent(Map<String, Object> map) {

      }
    };
  }


  private VWOLogger getCustomLogger() {
    return new VWOLogger(VWOEnums.LOGGER_LEVEL.DEBUG.value()) {
      @Override
      public void trace(String var1, Object... var2) {

      }

      @Override
      public void debug(String var1, Object... var2) {

      }

      @Override
      public void info(String var1, Object... var2) {

      }

      @Override
      public void warn(String var1, Object... var2) {

      }

      @Override
      public void error(String var1, Object... var2) {

      }
    };
  }

  private Storage.User getUSerStorage() {
    return new Storage.User() {
      @Override
      public Map<String, String> get(String userId, String campaignKey) throws Exception {
        return null;
      }

      @Override
      public void set(Map<String, String> userData) throws Exception {

      }
    };
  }

}
