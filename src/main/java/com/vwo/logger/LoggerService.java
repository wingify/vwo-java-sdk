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

package com.vwo.logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LoggerService {

  private static final Logger LOGGER = Logger.getLogger(LoggerService.class);

  public Map<String, String> debugMessages;
  public Map<String, String> errorMessages;
  public Map<String, String> infoMessages;
  public Map<String, String> warningMessages;

  // singleton instance should be private and should be accessed only by getInstance() to avoid getting NLP's
  // public static LoggerService instance;
  private static LoggerService instance = null;

  public static LoggerService getInstance() {
    // instantiate the singleton object if not already done
    if (instance == null) {

      // synchronize this instantiation code, to prevent multiple instantiations and make it thread safe
      synchronized (LoggerService.class) {
        // check for instance being null again, to ensure that multiple instances are not created by multiple threads (edge case)
        if (instance == null) {
          instance = new LoggerService();
        }
      }

      // return new LoggerService();
    }

    return instance;
  }

  // constructor for a singleton should be private, since it is only called from a nember function
  // [THIS CAN ALSO CAUSE LOTS OF TESTS TO BREAK, SO PLEASE RUN ALL UNIT/FUNCTIONAL TESTS FOR THIS]
  // public LoggerService() {
  private LoggerService() {
    debugMessages = readLogsData("debug-messages.json");
    infoMessages = readLogsData("info-messages.json");
    errorMessages = readLogsData("error-messages.json");
    warningMessages = readLogsData("warning-messages.json");
  }

  private Map<String, String> readLogsData(String fileName) {
    try {
      InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
      return new ObjectMapper().readValue(inputStream, Map.class);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new HashMap<String, String>();
  }


  public static String getComputedMsg(String msg, Map<String, String> map) {
    for (Map.Entry<String, String> pair : map.entrySet()) {
      String key = pair.getKey();
      String value = pair.getValue();
      try {
        msg = msg.replaceAll("\\{" + key + "}", value.replaceAll("\\$", "\\\\\\$"));
      } catch (Exception e) {
        LOGGER.error("Exception occurred while logging the value: " + value);
      }
    }
    return msg;
  }
}
