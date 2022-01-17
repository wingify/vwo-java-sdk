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
  public static LoggerService instance;

  public static LoggerService getInstance() {
    if (instance == null) {
      return new LoggerService();
    }
    return instance;
  }

  public LoggerService() {
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
