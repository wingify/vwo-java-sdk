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

package com.vwo.tests.unit.PreSegmentationTests;

import com.vwo.services.segmentation.PreSegmentation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StartsWithOperandTests {

  @Test
  public void exactMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "something");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void prefixMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "somethingdfgdwerewew");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void suffixMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "asdsdsdsomething");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void containsMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "asdn3kn42knsdsomethingmm");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void specialCharactersTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(f25u!v@b#k$6%9^f&o*v(m)w_-=+s,./`(*&^%$#@!*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "f25u!v@b#k$6%9^f&o*v(m)w_-=+s,./`(*&^%$#@!---A-N-Y-T-H-I-N-G---");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void spacesTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(nice to see you. will    you be   my        friend?*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "nice to see you. will    you be   my        friend? Great!!");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void upperCaseTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(HgUvshFRjsbTnvsdiUFFTGHFHGvDRT.YGHGH*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "HgUvshFRjsbTnvsdiUFFTGHFHGvDRT.YGHGH---A-N-Y-T-H-I-N-G---");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void numericDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 123456);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void floatDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 123.45);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void floatDataTypeTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123.456*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 123.456789);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void numericDataTypeMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 123.0);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void stringifiedFloatTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123.456*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "123.456789");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void charDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(E*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 'E');
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(true*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", true);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(false*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", false);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void mismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "qwertyu");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void partOfTextTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(zzsomethingzz*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void singleCharTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(zzsomethingzz*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "i");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void CaseMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "Something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void CaseMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "SOMETHING");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void noValueProvidedTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void missingkeyValueTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void nullValueProvidedTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", null);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void incorrectKeyTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("neq", "something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void incorrectKeyCaseTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("EQ", "something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void numericDataTypeMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 12);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void floatDataTypeMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(123.456*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 123);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(false*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", true);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(true*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", false);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void charDataTypeCaseMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(E*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 'e');
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void charDataTypeCaseMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(e*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 'E');
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  public static void verifyExpectation(String dsl, Map<String, Object> customVariables) {
    assertEquals(PreSegmentation.isPresegmentValid(dsl, customVariables), customVariables.get("expectation"));
  }
}
