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

package com.vwo.tests.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.logger.Logger;
import com.vwo.logger.VWOLogger;
import com.vwo.services.http.HttpRequestBuilder;
import com.vwo.services.settings.SettingsFileUtil;
import com.vwo.services.storage.Storage;
import com.vwo.tests.data.Settings;
import org.junit.jupiter.api.Test;

import java.util.Map;


public class APITests {
  private static final Logger LOGGER = Logger.getLogger(APITests.class);
  private static int accountId = 123456;


  @Test
  public void hasRequiredModules() throws NoSuchMethodException {
    LOGGER.info("Should export required modules and APIs");

    Class[] launchArg = new Class[1];
    launchArg[0] = String.class;

    Class[] getSettingsArg = new Class[2];
    getSettingsArg[0] = String.class;
    getSettingsArg[1] = String.class;

    assertTrue(VWO.class.getMethod("launch", launchArg) != null);
    assertTrue(VWO.class.getMethod("getSettingsFile", getSettingsArg) != null);

    Class vwoClass = VWO.launch("").getClass();

    Class[] userStorageArg = new Class[1];
    userStorageArg[0] = Storage.User.class;
    assertTrue(vwoClass.getMethod("withUserStorage", userStorageArg) != null);

    Class[] devModeArg = new Class[1];
    devModeArg[0] = boolean.class;
    assertTrue(vwoClass.getMethod("withDevelopmentMode", devModeArg) != null);

    Class[] customLoggerArg = new Class[1];
    customLoggerArg[0] = VWOLogger.class;
    assertTrue(vwoClass.getMethod("withCustomLogger", customLoggerArg) != null);
    assertTrue(vwoClass.getMethod("build", null) != null);

    Class vwoInstanceClass = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_50_50).build().getClass();

    Class[] variationArg = new Class[2];
    variationArg[0] = String.class;
    variationArg[1] = String.class;
    assertTrue(vwoInstanceClass.getMethod("activate", variationArg) != null);
    assertTrue(vwoInstanceClass.getMethod("getVariationName", variationArg) != null);

    Class[] trackArg = new Class[3];
    trackArg[0] = Object.class;
    trackArg[1] = String.class;
    trackArg[2] = String.class;
    assertTrue(vwoInstanceClass.getMethod("track", trackArg) != null);
  }
}
