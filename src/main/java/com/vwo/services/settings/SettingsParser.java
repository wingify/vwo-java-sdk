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

package com.vwo.services.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.models.response.Settings;

import java.io.IOException;

public class SettingsParser implements Parser {
  private ObjectMapper objectMapper;

  public SettingsParser(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Settings parseSettingsFile(String settingsFile) throws Exception {
    try {
      Settings settings = objectMapper.readValue(settingsFile, Settings.class);
      return settings;
    } catch (IOException e) {
      throw new Exception("Unable to parse Setting file: " + settingsFile, e);
    }
  }
}
