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

public class ContainsOperandTests {

  @Test
  public void exactMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "something");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void suffixMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "asdn3kn42knsdsomething");
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
  public void containsMatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "asdn3kn42knsdsomethingjsbdj");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void specialCharactersTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*f25u!v@b#k$6%9^f&o*v(m)w_-=+s,./`(*&^%$#@!*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "A-N-Y-T-H-I-N-G---f25u!v@b#k$6%9^f&o*v(m)w_-=+s,./`(*&^%$#@!----A-N-Y-T-H-I-N-G");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void containsOperandFalsyTestWithSpecialCharacter() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"a\":\"wildcard(*some*thing*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("a", "hellosomethingworld");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void spacesTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*nice to see you. will    you be   my        friend?*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "Hello there!! nice to see you. will    you be   my        friend? Yes, Great!!");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void upperCaseTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*HgUvshFRjsbTnvsdiUFFTGHFHGvDRT.YGHGH*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "A-N-Y-T-H-I-N-G---HgUvshFRjsbTnvsdiUFFTGHFHGvDRT.YGHGH----A-N-Y-T-H-I-N-G");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void numericDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 365412363);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void floatDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*123.456*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 765123.4567364);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void numericDataTypeMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 765123.7364);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void stringifiedFloatTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*123.456*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "87654123.4567902");
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void charDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*E*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 'E');
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*true*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", true);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*false*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", false);
      put("expectation", true);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void mismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "qwertyu");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void partOfTextTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*zzsomethingzz*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void singleCharTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*zzsomethingzz*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "i");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void CaseMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "Something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void CaseMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "SOMETHING");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void noValueProvidedTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", "");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void missingkeyValueTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void nullValueProvidedTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", null);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void incorrectKeyTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("neq", "something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void incorrectKeyCaseTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*something*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("EQ", "something");
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void numericDataTypeMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*123*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 12);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*false*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", true);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void booleanDataTypeMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*true*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", false);
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void charDataTypeCaseMismatchTest() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*E*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 'e');
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  @Test
  public void charDataTypeCaseMismatchTest2() {
    String dsl = "{\"or\":[{\"custom_variable\":{\"eq\":\"wildcard(*e*)\"}}]}";
    Map<String, Object> customVariables = new HashMap<String, Object>() {{
      put("eq", 'E');
      put("expectation", false);
    }};

    verifyExpectation(dsl, customVariables);
  }

  public static void verifyExpectation(String dsl, Map<String, Object> customVariables) {
    assertEquals(PreSegmentation.isPresegmentValid(dsl, customVariables, "", ""), customVariables.get("expectation"));
  }
}
