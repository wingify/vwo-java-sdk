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

package com.vwo.tests.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.vwo.VWO;
import com.vwo.logger.Logger;
import com.vwo.models.response.BatchEventData;
import com.vwo.services.batch.FlushInterface;
import com.vwo.tests.data.Settings;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class EventBatchingTests {
  private static VWO vwoInstance;
  private static String userId = TestUtils.getRandomUser();
  BatchEventData batchEventData = new BatchEventData();
  private static final Logger LOGGER = Logger.getLogger(EventBatchingTests.class);

  @Test
  public void validationTests() throws NullPointerException {
    LOGGER.info("Event Batching Queue should be undefined if batchEventsData is not passed");
    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
    assertNull(vwoInstance.getBatchEventQueue());

    LOGGER.info("Event batching Queue should be defined if batchEventsData is passed");
    batchEventData.setEventsPerRequest(20);
    batchEventData.setRequestTimeInterval(200);
    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).withBatchEvents(batchEventData).build();
    assertNotEquals(vwoInstance.getBatchEventQueue(), null);
  }

  @Test
  public void enqueueAndFlushEventsTests() throws NullPointerException, NoSuchFieldException {
    LOGGER.info("Event Batching: enqueue should queue an event, flushEvents should flush queue");
    batchEventData.setEventsPerRequest(10);
    batchEventData.setRequestTimeInterval(100);
    batchEventData.setFlushCallback(new FlushInterface() {
      @Override
      public void onFlush(String error, JsonNode events) {
        assertEquals(events.get("ev").size(), 1);
      }
    });
    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withBatchEvents(batchEventData).build();
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
    vwoInstance.activate("AB_TRAFFIC_100_WEIGHT_33_33_33", userId);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
    vwoInstance.getBatchEventQueue().flush(false);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
  }

  @Test
  public void flushQueueOnMaxEventsTest() throws InterruptedException {
    LOGGER.info("Event Batching: queue should be flushed if eventsPerRequest is reached");
    batchEventData.setEventsPerRequest(2);
    batchEventData.setRequestTimeInterval(100);
    batchEventData.setFlushCallback(new FlushInterface() {
      @Override
      public void onFlush(String error, JsonNode events) {
        assertEquals(events.get("ev").size(), 2);
      }
    });
    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withBatchEvents(batchEventData).build();
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
    vwoInstance.activate("AB_TRAFFIC_100_WEIGHT_33_33_33", userId);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
    vwoInstance.track("AB_TRAFFIC_100_WEIGHT_33_33_33", userId, "CUSTOM");
    Thread.sleep(2000);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
  }

  @Test
  public void flushQueueOnTimerExpiredTest() throws  InterruptedException {
    LOGGER.info("Event Batching: queue should be flushed if requestTimeInterval is reached");
    batchEventData.setEventsPerRequest(5);
    batchEventData.setRequestTimeInterval(5);
    batchEventData.setFlushCallback(new FlushInterface() {
      @Override
      public void onFlush(String error, JsonNode events) {
        assertEquals(events.get("ev").size(), 2);
      }
    });
    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withBatchEvents(batchEventData).build();
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
    vwoInstance.activate("AB_TRAFFIC_100_WEIGHT_33_33_33", userId);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
    vwoInstance.push("tagKey", "tagValue", userId);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 2);
    Thread.sleep(6000);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
  }

  @Test
  public void flushEventsAPITest() {
    LOGGER.info("Event Batching: enqueue should queue an event, flushEvents should flush queue");
    batchEventData.setEventsPerRequest(5);
    batchEventData.setRequestTimeInterval(100);
    batchEventData.setFlushCallback(new FlushInterface() {
      @Override
      public void onFlush(String error, JsonNode events) {
        assertEquals(events.get("ev").size(), 1);
      }
    });
    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withBatchEvents(batchEventData).build();
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
    vwoInstance.activate("AB_TRAFFIC_100_WEIGHT_33_33_33", userId);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
    boolean isQueueFlushed = vwoInstance.flushEvents();
    assertEquals(isQueueFlushed, true);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 0);
  }
}
