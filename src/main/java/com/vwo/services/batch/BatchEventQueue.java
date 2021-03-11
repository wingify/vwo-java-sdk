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

package com.vwo.services.batch;

import com.vwo.enums.EventEnum;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.models.BatchEventData;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpPostRequest;
import com.vwo.services.http.HttpRequestBuilder;

import java.util.Queue;
import java.util.Map;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;

public class BatchEventQueue {
  public static final int MAX_EVENTS_PER_REQUEST = 5000;
  Queue<Map<String, Object>> batchQueue = new LinkedList<Map<String, Object>>();
  private static final Map<String, Integer> queueMetaData = new HashMap<String, Integer>();
  int requestTimeInterval = 600; //default:- 10 * 60(secs) = 600 secs i.e. 10 minutes
  int eventsPerRequest = 100;  //default
  FlushInterface flushCallback;
  Timer timer;
  private static final Logger LOGGER = Logger.getLogger(BatchEventQueue.class);
  private final int accountId;
  private final String apikey;
  private boolean isDevelopmentMode;
  private boolean isBatchProcessing = false;

  /**
   * Init variables in BatchEventQueue.
   *
   * @param batchEvents       BatchEventsData instance
   * @param apikey            VWO application apikey
   * @param accountId         VWO application accountId
   * @param isDevelopmentMode Boolean value specifying development mode is on ir off
   */
  public BatchEventQueue(BatchEventData batchEvents, String apikey, int accountId, boolean isDevelopmentMode) {
    if (batchEvents.getRequestTimeInterval() > 1) {
      this.requestTimeInterval = batchEvents.getRequestTimeInterval();
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.REQUEST_TIME_INTERVAL_OUT_OF_BOUNDS.value(new HashMap<String, String>() {
        {
          put("min_value", "1");
          put("default_value", String.valueOf(requestTimeInterval));
        }
      }));
    }

    if (batchEvents.getEventsPerRequest() > 0 && batchEvents.getEventsPerRequest() <= MAX_EVENTS_PER_REQUEST) {
      this.eventsPerRequest = Math.min(batchEvents.getEventsPerRequest(), MAX_EVENTS_PER_REQUEST);
    } else {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.EVENTS_PER_REQUEST_OUT_OF_BOUNDS.value(new HashMap<String, String>() {
        {
          put("min_value", "0");
          put("max_value", String.valueOf(MAX_EVENTS_PER_REQUEST));
          put("default_value", String.valueOf(eventsPerRequest));
        }
      }));
    }

    if (batchEvents.getFlushCallback() != null) {
      this.flushCallback = batchEvents.getFlushCallback();
    }

    this.accountId = accountId;
    this.isDevelopmentMode = isDevelopmentMode;
    this.apikey = apikey;
  }

  /**
   * Initialize a new Timer.
   */
  private void createNewBatchTimer() {
    timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        flush(false);
      }
    };
    timer.schedule(task, requestTimeInterval * 1000L);
  }

  /**
   * Insert the event in the queue and flush if the queue is full.
   *
   * @param event Map containing configs of the event
   */
  public void enqueue(Map<String, Object> event) {
    if (isDevelopmentMode) {
      return;
    }

    batchQueue.add(event);
    addEventCount((Integer) event.get("eT"));
    LOGGER.info(LoggerMessagesEnums.INFO_MESSAGES.IMPRESSION_SUCCESS_QUEUE.value());
    if (timer == null) {
      createNewBatchTimer();
    }
    if (eventsPerRequest == batchQueue.size()) {
      flush(false);
    }
  }

  /**
   * Flush the queue, clear timer and send POST network call to VWO servers.
   *
   * @param manual Boolean specifying flush is triggered manual or not.
   * @return Boolean value specifying flush was successful or not.
   */
  public boolean flush(boolean manual) {
    if (batchQueue.size() == 0) {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.EVENT_QUEUE_EMPTY.value());
    }
    if (batchQueue.size() > 0 && !isBatchProcessing) {
      isBatchProcessing = true;
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.BEFORE_FLUSHING.value(new HashMap<String, String>() {
        {
          put("manually", manual ? "manually" : "");
          put("length", String.valueOf(batchQueue.size()));
          put("accountId", String.valueOf(accountId));
          put("timer", manual ? "Timer will be cleared and registered again" : "");
          put("queue_metadata", String.valueOf(queueMetaData));
        }
      }));
      boolean response = sendPostCall(manual);
      disposeData();
      return response;
    }
    clearRequestTimer();
    return true;
  }

  /**
   * CLears the timer.
   */
  public void clearRequestTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
      isBatchProcessing = false;
    }
  }

  /**
   * Flush Queue and clear timer.
   *
   * @return Boolean value specifying flush was successful or not.
   */
  public boolean flushAndClearInterval() {
    return flush(true);
  }

  /**
   * Send Post network call to VWO servers.
   *
   * @param sendSyncRequest Boolean specifying network should be sync or async.
   * @return Boolean value specifying flush was successful or not.
   */
  private boolean sendPostCall(boolean sendSyncRequest) {
    try {
      HttpParams httpParams = HttpRequestBuilder.getBatchEventPostCallParams(String.valueOf(accountId), apikey, batchQueue, flushCallback);
      LOGGER.debug(LoggerMessagesEnums.INFO_MESSAGES.AFTER_FLUSHING.value(new HashMap<String, String>() {
        {
          put("manually", sendSyncRequest ? "manually" : "");
          put("length", String.valueOf(batchQueue.size()));
          put("queue_metadata", String.valueOf(queueMetaData));

        }
      }));
      return HttpPostRequest.send(httpParams, accountId, sendSyncRequest);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
      return false;
    }
  }

  /**
   * Increase the count of a particular event.
   * @param eventType Type of the event
   */
  private void addEventCount(int eventType) {
    if (eventType == EventEnum.EVENT_TYPES.TRACK_USER.getValue()) {
      if (queueMetaData.containsKey("visitorEvents")) {
        queueMetaData.put("visitorEvents", queueMetaData.get("visitorEvents") + 1);
      } else {
        queueMetaData.put("visitorEvents", 1);
      }
    }
    if (eventType == EventEnum.EVENT_TYPES.TRACK_GOAL.getValue()) {
      if (queueMetaData.containsKey("goalEvents")) {
        queueMetaData.put("goalEvents", queueMetaData.get("goalEvents") + 1);
      } else {
        queueMetaData.put("goalEvents", 1);
      }
    }
    if (eventType == EventEnum.EVENT_TYPES.PUSH.getValue()) {
      if (queueMetaData.containsKey("pushEvents")) {
        queueMetaData.put("pushEvents", queueMetaData.get("pushEvents") + 1);
      } else {
        queueMetaData.put("pushEvents", 1);
      }
    }
  }

  /**
   * clear the queues and reset the timer.
   */
  private void disposeData() {
    batchQueue.clear();
    queueMetaData.clear();
    clearRequestTimer();
  }

  public Queue<Map<String, Object>> getBatchQueue() {
    return this.batchQueue;
  }
}
