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

import com.vwo.logger.Logger;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class BatchEventQueueDataObject {
  private static ConcurrentLinkedQueue<Map<String, Object>> batchQueue = new 
      ConcurrentLinkedQueue<>();

  private static final Logger LOGGER = Logger.getLogger(BatchEventQueueDataObject.class);

  // constructor
  public BatchEventQueueDataObject() {}
  
  /**
   * Call this function to add event to batch queue.

   * @param event to add to batch queue
   */
  public void add(Map<String, Object> event) {
    batchQueue.add(event);
  }
  
  /**
   * Call this function to return a copy of the batch queue while popping elements from the main
   * batch queue.

   * @param sizeThreshold the number of elements to be copied
   * @return copied batch queue
   */
  public LinkedList<Map<String, Object>> popBatchQueue(int sizeThreshold) {
    LinkedList<Map<String, Object>> newBatchQueue;
    
    synchronized (BatchEventQueueDataObject.class) {
      // pop the given number of events from queue
      newBatchQueue = new LinkedList<Map<String, Object>>();
      for (int x = 0; !batchQueue.isEmpty() && batchQueue.size() > 0 && x < sizeThreshold; x++) {
        try {
          newBatchQueue.add(batchQueue.poll());
        } catch (NoSuchElementException e) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e1) {
            LOGGER.error("InterruptedException");
          }
        }
      }
    }

    return newBatchQueue;
  }
  
  public Queue<Map<String, Object>> getBatchEventQueue() {
    return batchQueue;
  }
}
