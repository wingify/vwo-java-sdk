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

package com.vwo.services.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.vwo.enums.EventEnum;
import com.vwo.logger.Logger;
import com.vwo.logger.LoggerService;
import com.vwo.models.response.BatchEventData;
import com.vwo.services.http.HttpParams;
import com.vwo.services.http.HttpPostRequest;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.services.http.PostResponseHandler;
import com.vwo.utils.HttpUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class BatchEventQueue implements PostResponseHandler {
  public static final int MAX_EVENTS_PER_REQUEST = 5000;
  private static BatchEventQueueDataObject batchEventQueueObj = null;
  private static final Map<String, Integer> queueMetaData = new HashMap<String, Integer>();
  int requestTimeInterval = 600; //default:- 10 * 60(secs) = 600 secs i.e. 10 minutes
  int eventsPerRequest = 100;  //default
  FlushInterface flushCallback;
  private static final Logger LOGGER = Logger.getLogger(BatchEventQueue.class);
  private final int accountId;
  private final String apikey;
  private boolean isDevelopmentMode;
  private Map<String, Integer> usageStats;
  
  // timer related
  private static Timer timer = null;
  private static long timerInterval = 0;
  
  // thread safety related
  private static Lock lock = new ReentrantLock();

  /**
   * Init variables in BatchEventQueue.
   *
   * @param batchEvents       BatchEventsData instance
   * @param apikey            VWO application apikey
   * @param accountId         VWO application accountId
   * @param isDevelopmentMode Boolean value specifying development mode is on ir off
   * @param usageStats        usage info collected at the time of VWO instantiation.
   */
  public BatchEventQueue(BatchEventData batchEvents, String apikey, int accountId, boolean isDevelopmentMode, Map<String, Integer> usageStats) {
    this.accountId = accountId;
    this.isDevelopmentMode = isDevelopmentMode;
    this.apikey = apikey;
    this.usageStats = usageStats;

    // set request time interval
    if (batchEvents.getRequestTimeInterval() > 1) {
      this.requestTimeInterval = batchEvents.getRequestTimeInterval();
    } else {
        //      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.REQUEST_TIME_INTERVAL_OUT_OF_BOUNDS
        // .value(new HashMap<String, String>() {
        //        {
        //          put("min_value", "1");
        //          put("default_value", String.valueOf(requestTimeInterval));
        //        }
        //      }));
    }

    // set events per request
    if (batchEvents.getEventsPerRequest() > 0 
        && batchEvents.getEventsPerRequest() <= MAX_EVENTS_PER_REQUEST) {
      this.eventsPerRequest = Math.min(batchEvents.getEventsPerRequest(), MAX_EVENTS_PER_REQUEST);
    } else {
        //      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.EVENTS_PER_REQUEST_OUT_OF_BOUNDS
        // .value(new HashMap<String, String>() {
        //        {
        //          put("min_value", "0");
        //          put("max_value", String.valueOf(MAX_EVENTS_PER_REQUEST));
        //          put("default_value", String.valueOf(eventsPerRequest));
        //        }
        //      }));
    }

    // set flush callback
    if (batchEvents.getFlushCallback() != null) {
      this.flushCallback = batchEvents.getFlushCallback();
    }
    
    // update timer if it was already set but interval value is different
    if (timer != null && batchEvents.getRequestTimeInterval() > 1
        && timerInterval != batchEvents.getRequestTimeInterval()) {
      this.requestTimeInterval = batchEvents.getRequestTimeInterval();
      
      // reset timer with new interval value
      clearRequestTimer();
      createNewBatchTimer();
    }
    
    // instantiate batch event queue object if not already done
    if (batchEventQueueObj == null) {
      // synchronize to make thread safe
      synchronized (BatchEventQueue.class) {
        if (batchEventQueueObj == null) {
          batchEventQueueObj = new BatchEventQueueDataObject();
        }
      }
    }
  }

  /**
   * Initialize a new Timer.
   */
  private void createNewBatchTimer() {
    // create only if timer is null
    if (timer == null) {
      // synchronize to make thread safe
      synchronized (BatchEventQueue.class) {
        if (timer == null) {
          // instantiate a new timer
          timer = new Timer();
          TimerTask task = new TimerTask() {
            @Override
            public void run() {
              // timer calls flush
              flush(false);
            }
          };

          // schedule the timer to repeat in specified time interval
          timerInterval = requestTimeInterval * 1000L;
          timer.schedule(task, timerInterval, timerInterval);
        }
      }
    }
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
    
    // enqueue event to batch queue
    batchEventQueueObj.add(event);
    
    LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages
        .get("EVENT_QUEUE"), new HashMap<String, String>() {
          {
            put("event", event.toString());
            put("queueType", "batch");
          }
        }));

    // instantiate timer
    if (timer == null) {
      createNewBatchTimer();
    }

    // flush if events per request threshold crossed
    if (batchEventQueueObj.getBatchEventQueue().size() >= eventsPerRequest) {
      flush(false);
    }
  }

  /**
   * Flush the queue and send POST network call to VWO servers.
   *
   * @param manual Boolean specifying flush is triggered manual or not.
   * @return Boolean value specifying flush was successful or not.
   */
  public boolean flush(boolean manual) {
    Queue<Map<String, Object>> batchEventQueueToFlush;
    Iterator<Map<String, Object>> iterator;
    boolean response = false;

    // edge case when queue size is 0
    if (batchEventQueueObj.getBatchEventQueue().size() == 0) {
      // LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.EVENT_QUEUE_EMPTY.value());
    }

    // request for lock before flushing to make this thread safe
    // but to not affect performance, don't wait for lock to release
    if (lock.tryLock()) {
      // proceed if batch event queue has any events
      if (batchEventQueueObj.getBatchEventQueue().size() >= 0) {
        // copy batch queue to send as param to sendPostCall()
        batchEventQueueToFlush = batchEventQueueObj.popBatchQueue(eventsPerRequest);
        final int size = batchEventQueueToFlush.size();
              
        // logger debug
        LOGGER.debug(LoggerService.getComputedMsg(
            LoggerService.getInstance().debugMessages.get("EVENT_BATCH_BEFORE_FLUSHING"),
            new HashMap<String, String>() {
            {
              put("manually", manual ? "manually" : "");
              // put("length", String.valueOf(batchQueue.size()));
              put("length", String.valueOf(size));
              put("accountId", String.valueOf(accountId));
              put("timer", manual ? "Timer will be cleared and registered again" : "");
              // put("queue_metadata", String.valueOf(queueMetaData));
            }
          }));
              
        // update event counts by iterating through the event batch queue and updating count
        iterator = batchEventQueueToFlush.iterator();
        while (iterator.hasNext()) {
          addEventCount((Integer) iterator.next().get("eT"));
        }

        // send events to backend server
        response = sendPostCall(batchEventQueueToFlush, manual);
              
        // post processing after sending events to backend server
        if (response) {
          // clear the meta data (WHY ARE WE MAINTING THIS METADATA ANYWAYS?)
          queueMetaData.clear();
        } else {
          LOGGER.error("Network call failed for account " + accountId);
        }
      }
          
      // release lock
      lock.unlock();
    }
    
    return response;
  }

  /**
   * CLears the timer.
   */
  public void clearRequestTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  /**
   * Flush Queue and clear timer.
   *
   * @return Boolean value specifying flush was successful or not.
   */
  public boolean flushAndClearInterval() {
    boolean isSuccess;
    
    // clear timer
    timer = null;
    
    // request flushing till batch queue is cleared of all events
    do {
      isSuccess = flush(true);
      
      // check if batch queue cleared
      if (batchEventQueueObj.getBatchEventQueue() != null
          && batchEventQueueObj.getBatchEventQueue().size() > 0) {
        // sleep before trying again
        try {
          Thread.sleep(400);
        } catch (InterruptedException e) {
          // no need to handle this exception
        }
      }
    } while (batchEventQueueObj.getBatchEventQueue() != null
        && batchEventQueueObj.getBatchEventQueue().size() > 0);
    
    return isSuccess;
  }

  /**
   * Send Post network call to VWO servers.
   *
   * @param sendSyncRequest Boolean specifying network should be sync or async.
   * @param batchEventQueueToFlush is the queue to be flushed
   * @return Boolean value specifying flush was successful or not.
   */
  private boolean sendPostCall(Queue<Map<String, Object>> batchEventQueueToFlush, boolean sendSyncRequest) {
    boolean isSuccess = false;

    // make http call if batch event queue is not empty
    if (!batchEventQueueToFlush.isEmpty()) {
      try {
        HttpParams httpParams = HttpRequestBuilder.getBatchEventPostCallParams(String.valueOf(
            accountId), apikey, batchEventQueueToFlush, this.usageStats);
        
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get(
            "EVENT_BATCH_After_FLUSHING"), new HashMap<String, String>() {
                  {
                    put("manually", sendSyncRequest ? "manually" : "");
                    // put("length", String.valueOf(batchQueue.size()));
                    put("length", String.valueOf(batchEventQueueToFlush.size()));
                  }
            }));
            
        isSuccess = HttpPostRequest.send(httpParams, this, sendSyncRequest);
      } catch (Exception e) {
        // LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.UNABLE_TO_DISPATCH_HTTP_REQUEST.value());
      }
    } else {
      // set return flag to true since network call is not being made
      isSuccess = true;
    }
    
    return isSuccess;
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

  public Queue<Map<String, Object>> getBatchQueue() {
    return batchEventQueueObj.getBatchEventQueue();
  }

  @Override
  public void onResponse(String endpoint, int status, HttpResponse response, JsonNode events)
      throws IOException {
    if (status >= 200 && status < 300) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("IMPRESSION_BATCH_SUCCESS"), new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
          put("endPoint", HttpUtils.getModifiedLogRequest(endpoint));
        }
      }));
      if (flushCallback != null) {
        flushCallback.onFlush(null, events);
      }
    } else if (status == 413) {
      String error = EntityUtils.toString(response.getEntity());
      LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("CONFIG_BATCH_EVENT_LIMIT_EXCEEDED"), new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
          put("endPoint", endpoint);
          put("eventsPerRequest", String.valueOf(events.get("ev").size()));
        }
      }));
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("IMPRESSION_FAILED"), new HashMap<String, String>() {
        {
          put("endPoint", endpoint);
          put("err", error);
        }
      }));
      if (flushCallback != null) {
        flushCallback.onFlush(error, events);
      }
    } else {
      String error = EntityUtils.toString(response.getEntity());
      LOGGER.info(LoggerService.getInstance().infoMessages.get("IMPRESSION_BATCH_FAILED"));
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("IMPRESSION_FAILED"), new HashMap<String, String>() {
        {
          put("endPoint", endpoint);
          put("err", error);
        }
      }));
      if (flushCallback != null) {
        flushCallback.onFlush(error, events);
      }
    }
  }
}
