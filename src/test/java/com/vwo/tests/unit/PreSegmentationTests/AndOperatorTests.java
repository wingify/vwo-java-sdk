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

package com.vwo.tests.unit.PreSegmentationTests;

import com.vwo.services.segmentation.PreSegmentation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AndOperatorTests {

  @Test
  public void singleAndOperatorMatchingTest() {
    String dsl = "{\"and\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "eq_value");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void singleAndOperatorMismatchTest() {
    String dsl = "{\"and\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("a", "n_eq_value");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void singleAndOperatorCaseMismatchTest() {
    String dsl = "{\"and\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "Eq_Value");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void multipleAndOperatorTest2() {
    String dsl = "{\"and\":[{\"and\":[{\"and\":[{\"and\":[{\"and\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]}]}]}]}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "eq_value");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void multipleAndOperatorWithSingleCorrectValueTest() {
    String dsl = "{\"and\":[{\"or\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]},{\"or\":[{\"custom_variable\":{\"reg\":\"regex(myregex+)\"}}]}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "eq_value");
      put("reg", "wrong");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void multipleAndOperatorWithSingleCorrectValueTest2() {
    String dsl = "{\"and\":[{\"or\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]},{\"or\":[{\"custom_variable\":{\"reg\":\"regex(myregex+)\"}}]}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "wrong");
      put("reg", "myregexxxxxx");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void multipleAndOperatorWithAllCorrectCustomVariablesTest() {
    String dsl = "{\"and\":[{\"or\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]},{\"or\":[{\"custom_variable\":{\"reg\":\"regex(myregex+)\"}}]}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "eq_value");
      put("reg", "myregexxxxxx");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void multipleAndOperatorWithAllIncorrectCorrectCustomVariablesTest() {
    String dsl = "{\"and\":[{\"or\":[{\"custom_variable\":{\"eq\":\"eq_value\"}}]},{\"or\":[{\"custom_variable\":{\"reg\":\"regex(myregex+)\"}}]}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "wrong");
      put("reg", "wrong");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void sdhgsjdh() {
    String dsl = "{\"or\":[{\"user\":\"Varun\"}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "wrong");
      put("reg", "wrong");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  public static void verifyExpectation(String dsl, Map<String, Object> customVariables) {
    assertEquals(PreSegmentation.isPresegmentValid(dsl, customVariables, "", ""), customVariables.get("expectation"));
  }
}
