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
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PushSegmentTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(PushSegmentTests.class);

  @Test
  public void validationTests() throws IOException {
    LOGGER.info("Should return false if no tag key is passed");
    assertFalse(vwoInstance.push("", "tagValue", userId).get(""));
    assertTrue(vwoInstance.push(null, "tagValue", userId).size() == 0);

    LOGGER.info("Should return false if no tag value is passed");
    assertFalse(vwoInstance.push("tagKey", "", userId).get("tagKey"));
    assertTrue(vwoInstance.push("tagKey", null, userId).size() == 0);

    LOGGER.info("Should return false if no user ID is passed");
    assertFalse(vwoInstance.push("tagKey", "tagValue", "").get("tagKey"));
    assertFalse(vwoInstance.push("tagKey", "tagValue", null).get("tagKey"));

    LOGGER.info("Should return false if custom dimension is not passed/empty");
    assertTrue(vwoInstance.push(null, userId).size() == 0);
    assertTrue(vwoInstance.push(new HashMap<>(), userId).size() == 0);
  }

  @Test
  public void maxLengthTests() throws IOException {
    LOGGER.info("Should return false if tag key > 255.");
    String tag = "rtyhgvftyhbvfrtytghbvtdygttuh67ugvctf7ugct7guctfygbhgfdghjgfghgfcgfhbvcfghbfunbhgfbghgygbhjasgduyasgdudgiugtrdtfyujhgfdreswertyuijhgfdsadxfcgvhbnvcxdsertyuhjnbvcxdrftgyhbvcxdwqkegdyusgcbdysughbctyfugihbhvgcyrxdy5ugvhcghfgxdetsrdyfugvjchfgxdtrydufgvcfgxyugyhcg";
    assertFalse(vwoInstance.push(tag, "tagValue", userId).get(tag));

    LOGGER.info("Should return false if tag key > 255.");
    assertFalse(vwoInstance.push("tagKey", tag, userId).get("tagKey"));
  }

  @Test
  public void correctCustomVariablesTest() throws IOException {
    LOGGER.info("Should return true for valid input customVariables.");
    assertTrue(vwoInstance.push("myKey", "myValue", userId).size()  > 0);
  }
}
