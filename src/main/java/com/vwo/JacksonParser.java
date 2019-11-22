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

package com.vwo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.config.ConfigParseException;
import com.vwo.logger.LoggerManager;
import com.vwo.models.SettingFileConfig;

import java.io.IOException;

/**
 * This file JacksonParser.java has references from "Optimizely Java SDK, version 3.2.0", Copyright 2017-2019, Optimizely,
 * under Apache 2.0 License.
 * Source - https://github.com/optimizely/java-sdk/blob/master/core-api/src/main/java/com/optimizely/ab/config/parser/JacksonConfigParser.java
 */
public class JacksonParser implements Parser {
  private ObjectMapper objectMapper;
  private static final LoggerManager LOGGER = LoggerManager.getLogger(JacksonParser.class);

  public JacksonParser(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public SettingFileConfig isValid(String settingFile) throws ConfigParseException {
    try {
      /**
       * Took reference from StackOverflow (https://stackoverflow.com/) to:
       * Convert json string to java class object.
       * Author - StaxMan (https://stackoverflow.com/users/59501/staxman)
       * Source - https://stackoverflow.com/questions/10308452/how-to-convert-the-following-json-string-to-java-object
       */
      SettingFileConfig settingFileConfig = objectMapper.readValue(settingFile, SettingFileConfig.class);
      return settingFileConfig;
    } catch (IOException e) {
      throw new ConfigParseException("Unable to parse Setting file: " + settingFile, e);
    }
  }

}
