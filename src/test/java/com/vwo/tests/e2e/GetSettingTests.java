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

package com.vwo.tests.e2e;

import com.vwo.VWO;
import com.vwo.logger.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;


public class GetSettingTests {
  private static String accountId = "1234";
  private static String sdkKey = "12345678asdfghj";
  private static final Logger LOGGER = Logger.getLogger(GetSettingTests.class);


  @Test
  public void validationTests() {
    LOGGER.info("Should return null if no accountID is passed");
    assertEquals(VWO.getSettingsFile("", sdkKey), null);
    assertEquals(VWO.getSettingsFile(null, sdkKey), null);

    LOGGER.info("Should return null if no sdkKey is passed");
    assertEquals(VWO.getSettingsFile(accountId, ""), null);
    assertEquals(VWO.getSettingsFile(accountId, null), null);
  }

  @Test
  public void settingsNotFoundTest() {
    LOGGER.info("Should return null if settings file is not found");
    assertEquals(VWO.getSettingsFile(accountId, sdkKey), "{\"message\":\"Invalid api key\"}");
  }
}
