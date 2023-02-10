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

package com.vwo.tests.unit.PreSegmentationTests;

import com.vwo.services.segmentation.PreSegmentation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LessThanOperatorTests {
  @Test
  public void LessThanOperatorPass() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"lt(150)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 100);
      put("expectation", true);
    }};
    verifyExpectation(dsl,customVariables);
  }

  @Test
  public void LessThanOperatorFail() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"lt(150)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 200);
      put("expectation", false);
    }};
    verifyExpectation(dsl,customVariables);
  }

  @Test
  public void LessThanOperatorEqualValueFail() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"lt(150)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 150);
      put("expectation", false);
    }};
    verifyExpectation(dsl,customVariables);
  }

  @Test
  public void LessThanOperatorStringValueFail() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"lt(150)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq","abc");
      put("expectation", false);
    }};
    verifyExpectation(dsl,customVariables);
  }
  public static void verifyExpectation(String dsl, Map<String, Object> customVariables) {
    assertEquals(PreSegmentation.isPresegmentValid(dsl, customVariables, "", ""), customVariables.get("expectation"));
  }
}
