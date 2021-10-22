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

package com.vwo.tests.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.VWOAdditionalParams;
import com.vwo.logger.Logger;
import com.vwo.models.response.Settings;
import com.vwo.models.response.Variable;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeatureVariableTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_0).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(FeatureVariableTests.class);

  @Test
  public void validationTests() throws IOException {
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_0, Settings.class);
    String variableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(0).getKey();
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();

    LOGGER.info("Should return null if no campaignKey is passed");
    assertEquals(vwoInstance.getFeatureVariableValue("", variableKey, userId), null);

    LOGGER.info("Should return null if no userId is passed");
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, variableKey, ""), null);

    LOGGER.info("Should return null if no variable key is passed");
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, "", userId), null);

    LOGGER.info("Should return null if campaignKey is not found in settingsFile");
    assertEquals(vwoInstance.getFeatureVariableValue("NO_SUCH_CAMPAIGN_KEY", variableKey, userId), null);
  }

  @Test
  public void abCampaignType() throws IOException {
    LOGGER.info("Should return null if campaign type is not feature rollout or feature test");

    Settings settingsFile1 = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String campaignKey = settingsFile1.getCampaigns().get(0).getKey();
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, "Variable-key", TestUtils.getRandomUser()), null);
  }

  @Test
  public void featureRolloutStringTests() throws IOException {
    LOGGER.info("Should test against a string feature variable");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();
    String variableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(0).getKey();
    Object expectedVariableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(0).getValue();
    ArrayList<UserExpectations.Variation> userExpectations = UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40;

    for (int i = 0; i < userExpectations.size(); i++) {
      String variableValue = (String) vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i]);
      assertEquals(variableValue, expectedVariableKey);
    }
  }

  @Test
  public void featureRolloutIntegerTests() throws IOException {
    LOGGER.info("Should test against a integer feature variable");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();
    String variableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(1).getKey();
    Object expectedVariableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(1).getValue();
    ArrayList<UserExpectations.Variation> userExpectations = UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40;

    for (int i = 0; i < userExpectations.size(); i++) {
      Integer variableValue = (Integer) vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i]);
      assertEquals(variableValue, expectedVariableKey);
    }
  }

  @Test
  public void featureRolloutDoubleTests() throws IOException {
    LOGGER.info("Should test against a integer feature variable");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();
    String variableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(2).getKey();
    Object expectedVariableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(2).getValue();
    ArrayList<UserExpectations.Variation> userExpectations = UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40;

    for (int i = 0; i < userExpectations.size(); i++) {
      Double variableValue = (Double) vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i]);
      assertEquals(variableValue, expectedVariableKey);
    }
  }

  @Test
  public void featureRolloutBooleanTests() throws IOException {
    LOGGER.info("Should test against a integer feature variable");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();
    String variableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(3).getKey();
    Object expectedVariableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(3).getValue();
    ArrayList<UserExpectations.Variation> userExpectations = UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40;

    for (int i = 0; i < userExpectations.size(); i++) {
      Boolean variableValue = (Boolean) vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i]);
      assertEquals(variableValue, expectedVariableKey);
    }
  }

  @Test
  public void featureRolloutInvalidTypeCastingTests() throws IOException {
    LOGGER.info("Should test against a integer feature variable");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_INCORRECT_VARIABLE_TYPE).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_INCORRECT_VARIABLE_TYPE, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();

    for (Variable variableInfo: featureRolloutSettingsConfig.getCampaigns().get(0).getVariables()) {

      Object variableValue = null;
      Object expectedVariableValue = null;
      switch (variableInfo.getType()) {
        case "string":
          expectedVariableValue = variableInfo.getValue().toString();
          break;
        case "boolean":
          expectedVariableValue = Boolean.valueOf(variableInfo.getValue().toString());
          break;
        case "integer":
          expectedVariableValue = ((Double) variableInfo.getValue()).intValue();
          break;
        case "double":
          expectedVariableValue = Double.valueOf(variableInfo.getValue().toString());
          break;

      }
      variableValue = vwoInstance.getFeatureVariableValue(campaignKey, variableInfo.getKey(), TestUtils.getUsers()[0]);
      assertEquals(variableValue, expectedVariableValue);
    }
  }

  @Test
  public void featureRolloutVariableNotFoundTests() throws IOException {
    LOGGER.info("Should test against a non existing feature variable");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();
    String variableKey = "NON_EXISTING";
    ArrayList<UserExpectations.Variation> userExpectations = UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40;

    for (int i = 0; i < userExpectations.size(); i++) {
      Object variableValue = vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i]);
      assertNull(variableValue);
    }
  }

  @Test
  public void featureRolloutTraffic0Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 0");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_0, UserExpectations.TRAFFIC_0);
  }

  @Test
  public void featureRolloutTraffic25Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 25");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_25, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureRolloutTraffic50Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 50");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_50, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureRolloutTraffic75Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 75");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_75, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureRolloutTraffic100Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 100");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40);
  }

  /**
   * FEATURE TEST CASES
   */

  @Test
  public void featureTestTraffic0Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 0");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_0, UserExpectations.TRAFFIC_0, null);
  }

  @Test
  public void featureTestTraffic25Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 25");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_25, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_25_WEIGHT_10_20_30_40, null);
  }

  @Test
  public void featureTestTraffic50Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 50");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_50, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_50_WEIGHT_10_20_30_40, null);
  }

  @Test
  public void featureTestTraffic75Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 75");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_75, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40, null);
  }

  @Test
  public void featureTestTraffic100Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 100");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40, null);
  }

  @Test
  public void featureTestJSONTests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with JSON variables");
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_JSON_100).withDevelopmentMode(true).build();
    vwoInstance.isFeatureEnabled("FT_T_JSON_100_W_50_50", "Faizan");
    Map variableValue = (Map) vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE1", "Faizan");
    assertEquals("json", variableValue.get("type"));
    assertEquals("json", variableValue.get("value"));

    variableValue = (Map) vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE2", "Faizan");
    assertTrue(variableValue.get("json") instanceof Map);
    assertTrue(variableValue.get("json1") instanceof Map);


    vwoInstance.isFeatureEnabled("FT_T_JSON_100_W_50_50", "Chris");
    variableValue = (Map) vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE1", "Chris");
    assertTrue(variableValue.get("jsonArray") instanceof ArrayList);

    variableValue = (Map) vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE2", "Chris");
    assertTrue(variableValue.get("jsonArray") instanceof ArrayList);
    assertTrue(variableValue.get("jsonObject") instanceof Map);

    Object variableValue1 = vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE3", "Chris");
    assertNull(variableValue1);

    variableValue1 = vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE4", "Chris");
    assertNull(variableValue1);

    variableValue1 = vwoInstance.getFeatureVariableValue("FT_T_JSON_100_W_50_50", "JSON_VARIABLE5", "Chris");
    assertNull(variableValue1);
  }

  @Test
  public void featureTestDisabledTests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 100 and all variations disabled");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100_DISABLED, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40, null);
  }

  @Test
  public void preSegmentTruthyTest() throws IOException {
    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("a", 987.1234);
        put("hello", "world");
      }
    };

    featureTestTests(com.vwo.tests.data.Settings.PRE_SEGMENT_FEATURE_TEST_TRAFFIC_75, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_75_WEIGHT_10_20_30_40, new VWO.AdditionalParams().setCustomVariables(customVariables));
  }

  @Test
  public void preSegmentFalsyTest() throws IOException {
    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("a", 987.1234);
        put("hello", "asdsd");
      }
    };

    featureTestTests(com.vwo.tests.data.Settings.PRE_SEGMENT_FEATURE_TEST_TRAFFIC_75, UserExpectations.TRAFFIC_0, new VWO.AdditionalParams().setCustomVariables(customVariables));
  }

  @Test
  public void userWhitelistingFalsyTest() throws IOException {
    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("eq", "not_something");
      }
    };

    Map<String, Object> variationTargetingVariables = new HashMap<String, Object>() {
      {
        put("chrome", "true");
        put("safari", "false");
        put("browser", "firefox 106.69");
      }
    };

    featureTestTests(com.vwo.tests.data.Settings.USER_WHITELISTING_FEATURE_TEST_TRAFFIC_100, UserExpectations.TRAFFIC_0, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void userWhitelistingFalsyWithValidCustomDimensionTest() throws IOException {
    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("eq", "something");
      }
    };

    Map<String, Object> variationTargetingVariables = new HashMap<String, Object>() {
      {
        put("chrome", "true");
        put("safari", "false");
        put("browser", "firefox 106.69");
      }
    };

    featureTestTests(com.vwo.tests.data.Settings.USER_WHITELISTING_FEATURE_TEST_TRAFFIC_100, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void userWhitelistingTruthyTest() throws IOException {
    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("eq", "not_something");
      }
    };

    Map<String, Object> variationTargetingVariables = new HashMap<String, Object>() {
      {
        put("chrome", "false");
        put("safari", "true");
        put("browser", "chrome 107.107");
      }
    };

    featureTestTests(com.vwo.tests.data.Settings.USER_WHITELISTING_FEATURE_TEST_TRAFFIC_100, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void userWhitelistingSingleSegmentTruthyTest() throws IOException {
    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("eq", "something");
      }
    };

    Map<String, Object> variationTargetingVariables = new HashMap<String, Object>() {
      {
        put("chrome", "false");
      }
    };

    featureTestTests(com.vwo.tests.data.Settings.USER_WHITELISTING_FEATURE_TEST_TRAFFIC_100, UserExpectations.ALL_VARIATION_2, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }


  /**
   * PRIVATE FUNCTIONS
   */

  private void featureRolloutTests(String SettingsConfig, ArrayList<UserExpectations.Variation> userExpectations) throws IOException {
    vwoInstance = VWO.launch(SettingsConfig).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(SettingsConfig, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();
    String variableKey = featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(0).getKey();
    String expectedVariableValue = (String) featureRolloutSettingsConfig.getCampaigns().get(0).getVariables().get(0).getValue();

    for (int i = 0; i < userExpectations.size(); i++) {
      String variableValue = (String) vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i]);
      assertEquals(variableValue, userExpectations.get(i).getVariation() == null ? null : expectedVariableValue);
    }
  }

  private void featureTestTests(String SettingsConfig, ArrayList<UserExpectations.Variation> userExpectations, VWOAdditionalParams additionalParams) throws IOException {
    vwoInstance = VWO.launch(SettingsConfig).build();
    Settings featureTestSettingsConfig = new ObjectMapper().readValue(SettingsConfig, Settings.class);
    String campaignKey = featureTestSettingsConfig.getCampaigns().get(0).getKey();

    for (int i = 0; i < userExpectations.size(); i++) {
      for (int j = 0; j < featureTestSettingsConfig.getCampaigns().get(0).getVariations().get(0).getVariables().size(); j++) {
        String variableKey = featureTestSettingsConfig.getCampaigns().get(0).getVariations().get(0).getVariables().get(j).getKey();
        String variableType = featureTestSettingsConfig.getCampaigns().get(0).getVariations().get(0).getVariables().get(j).getType();
        Object variableValue = vwoInstance.getFeatureVariableValue(campaignKey, variableKey, TestUtils.getUsers()[i], additionalParams);

        String expectedVariation = userExpectations.get(i).getVariation();
        boolean isFeatureEnabled = false;
        if (expectedVariation != null) {
          isFeatureEnabled = featureTestSettingsConfig.getCampaigns().get(0).getVariations().stream().filter(variationObj -> variationObj.getName().equalsIgnoreCase(expectedVariation)).findFirst().get().getIsFeatureEnabled();
        }
        assertEquals(variableValue, expectedVariation == null ? null : UserExpectations.getVariableValueForVariation(expectedVariation, variableType, isFeatureEnabled));
      }
    }
  }
}
