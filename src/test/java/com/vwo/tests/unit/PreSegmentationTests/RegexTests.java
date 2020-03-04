/**
 * Copyright 2019-2020 Wingify Software Pvt. Ltd.
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

public class RegexTests {

  @Test
  public void regexOperandTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"reg\":\"regex(myregex+)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("reg", "myregexxxxxx");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void regexOperandTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"reg\":\"regex(<(W[^>]*)(.*?)>)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("reg", "<WingifySDK id=1></WingifySDK>");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void regexOperandMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"reg\":\"regex(<(W[^>]*)(.*?)>)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("reg", "<wingifySDK id=1></wingifySDK>");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void regexOperandCaseMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"reg\":\"regex(myregex+)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("reg", "myregeXxxxxx");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void invalidReqexTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"reg\":\"regex(*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("reg", "*");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void invalidReqexTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"reg\":\"regex(*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("reg", "asdf");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  public static void verifyExpectation(String dsl, Map<String, Object> customVariables) {
    assertEquals(PreSegmentation.isPresegmentValid(dsl, customVariables, "", ""), customVariables.get("expectation"));
  }
}
