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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.VWOAdditionalParams;
import com.vwo.models.response.BatchEventData;
import com.vwo.models.response.Settings;
import com.vwo.logger.Logger;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ActivateTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(ActivateTests.class);
  BatchEventData batchEventData = new BatchEventData();

  @Test
  public void validationTests() throws IOException {
    LOGGER.info("Should return null if no campaignKey is passed");
    assertNull(vwoInstance.activate("", userId));

    LOGGER.info("Should return null if no userId is passed");
    Settings settings = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    assertNull(vwoInstance.activate(settings.getCampaigns().get(0).getKey(), ""));

    LOGGER.info("Should return null if campaignKey is not found in settingsFile");
    assertNull(vwoInstance.activate("NO_SUCH_CAMPAIGN_KEY", userId));
  }

  @Test
  public void setting1Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, UserExpectations.AB_TRAFFIC_50_WEIGHT_50_50, null);
  }

  @Test
  public void featureRolloutCampaignTypeTests() throws IOException {
    LOGGER.info("Should return null for feature rollout type campaigns");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();

    assertNull(vwoInstance.activate(campaignKey, TestUtils.getRandomUser()));
  }

  @Test
  public void featureTestCampaignTypeTests() throws IOException {
    LOGGER.info("Should return null for feature test type campaigns");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100).build();

    assertNull(vwoInstance.activate(campaignKey, TestUtils.getRandomUser()));
  }

  @Test
  public void setting2Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50, UserExpectations.AB_TRAFFIC_100_WEIGHT_50_50, null);
  }

  @Test
  public void setting3Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:20-80");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_20_80, UserExpectations.AB_TRAFFIC_100_WEIGHT_20_80, null);
  }

  @Test
  public void setting4Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:20 and split:10-90");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_20_WEIGHT_10_90, UserExpectations.AB_TRAFFIC_20_WEIGHT_10_90, null);
  }

  @Test
  public void setting5Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:0-100");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_0_100, UserExpectations.AB_TRAFFIC_100_WEIGHT_0_100, null);
  }

  @Test
  public void setting6Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:33.3333:33.3333:33.3333");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.AB_TRAFFIC_100_WEIGHT_33_33_33, null);
  }

  @Test
  public void settingWith10VariationsTests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:75 with 10 variations of eqial weight");

    validateActivateMethod(com.vwo.tests.data.Settings.AB_TRAFFIC_75_VARIATIONS_10, UserExpectations.AB_TRAFFIC_75_VARIATIONS_10, null);
  }

  @Test
  public void preSegmentTruthyTest() throws IOException {
    LOGGER.info("Should test against a campaign with truthy pre segmentation");

    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("a", 987.1234);
        put("hello", "world");
      }
    };

    validateActivateMethod(com.vwo.tests.data.Settings.PRE_SEGMENT_AB_TEST_TRAFFIC_100, UserExpectations.AB_TRAFFIC_100_WEIGHT_50_50, new VWO.AdditionalParams().setCustomVariables(customVariables));
  }

  @Test
  public void preSegmentFalsyTest() throws IOException {
    LOGGER.info("Should test against a campaign with falsy pre segmentation");

    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("a", 987123);
        put("world", "hello");
      }
    };

    validateActivateMethod(com.vwo.tests.data.Settings.PRE_SEGMENT_AB_TEST_TRAFFIC_100, UserExpectations.TRAFFIC_0, new VWO.AdditionalParams().setCustomVariables(customVariables));
  }

  @Test
  public void userWhitelistingFalsyTest() throws IOException {
    LOGGER.info("Should test against a campaign with falsy user whitelisting");

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

    validateActivateMethod(com.vwo.tests.data.Settings.USER_WHITELISTING_AB_TEST_TRAFFIC_100, UserExpectations.TRAFFIC_0, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void whitelistingWithUserHash() throws IOException {
    LOGGER.info("Should return whitelisted variation as the user hash passes the whitelisting");

    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.USER_WHITELISTING_AB_TEST_TRAFFIC_100).withDevelopmentMode(true).build();
    Settings settings = vwoInstance.getSettingFile().getSettings();
    settings.getCampaigns().get(0).setPercentTraffic(0);
    VWOAdditionalParams params  = new VWOAdditionalParams();
    params.setCustomVariables(new HashMap<String, Object>(){{
      put("eq", "something");
    }});
    String variationName = vwoInstance.activate(settings.getCampaigns().get(0).getKey(), "Bill", params);
    assertEquals(variationName, null);

    settings.getCampaigns().get(0).setUserListEnabled(true);
    settings.getCampaigns().get(0).getVariations().get(1).setSegments(new HashMap<String, Object>() {{
      put("and", new ArrayList<Object>() {{
        add(new HashMap<String, Object>() {{
          put("user", "A242AF8A71655251BA55F9EBB1FB326F");
        }});
      }});
    }});

    variationName = vwoInstance.activate(settings.getCampaigns().get(0).getKey(), "Bill", params);
    assertEquals(variationName, "Variation-1");

    settings.getCampaigns().get(0).setPercentTraffic(100);
    settings.getCampaigns().get(0).getVariations().get(1).setSegments(new HashMap<String, Object>() {{
      put("and", new ArrayList<Object>() {{
        add(new HashMap<String, Object>() {{
          put("user", "5FBBE143B5B0584297909594993D7923");
        }});
      }});
    }});

    variationName = vwoInstance.activate(settings.getCampaigns().get(0).getKey(), "Bill", params);
    assertEquals(variationName, "Control");
  }

  @Test
  public void userWhitelistingFalsyWithValidCustomDimensionTest() throws IOException {
    LOGGER.info("Should test against a campaign with all truthy user whitelisting");

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

    validateActivateMethod(com.vwo.tests.data.Settings.USER_WHITELISTING_AB_TEST_TRAFFIC_100, UserExpectations.AB_TRAFFIC_100_WEIGHT_33_33_33, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void userWhitelistingTruthyTest() throws IOException {
    LOGGER.info("Should test against a campaign with all truthy user whitelisting");

    Map<String, Object> customVariables = new HashMap<String, Object>() {
      {
        put("eq", "something");
      }
    };

    Map<String, Object> variationTargetingVariables = new HashMap<String, Object>() {
      {
        put("chrome", "false");
        put("safari", "true");
        put("browser", "chrome 107.107");
      }
    };

    validateActivateMethod(com.vwo.tests.data.Settings.USER_WHITELISTING_AB_TEST_TRAFFIC_100, UserExpectations.AB_TRAFFIC_100_WEIGHT_33_33_33, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void userWhitelistingSingleSegmentTruthyTest() throws IOException {
    LOGGER.info("Should test against a campaign with only one truthy user whitelisting segment");

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

    validateActivateMethod(com.vwo.tests.data.Settings.USER_WHITELISTING_AB_TEST_TRAFFIC_100, UserExpectations.ALL_VARIATION_2, new VWO.AdditionalParams().setCustomVariables(customVariables).setVariationTargetingVariables(variationTargetingVariables));
  }

  @Test
  public void returningUserFlagPassedGloballyTest() throws IOException {
    LOGGER.info("should be/not added to eventBatching queue depending upon the flag passed while launching VWO.");
    batchEventData.setEventsPerRequest(50);

    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();


    //when the global value is not passed, by default false should be used.
    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withBatchEvents(batchEventData).build();
    vwoInstance.activate(campaignKey, "Ashley");
    vwoInstance.activate(campaignKey, "Ashley");
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 2);
  }

  @Test
  public void returningUserFlagPassedLocalTest() throws IOException {
    LOGGER.info("should return variation track user call should be sent if userStorageService is not passed");

    batchEventData.setEventsPerRequest(50);

    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withBatchEvents(batchEventData).build();

    VWOAdditionalParams params = new VWOAdditionalParams();
    vwoInstance.activate(campaignKey, "Ashley", params);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
    vwoInstance.activate(campaignKey, "Ashley", params);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 2);

    vwoInstance.activate(campaignKey, "Ashley", params);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 3);
    vwoInstance.activate(campaignKey, "Ashley", params);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 4);

    vwoInstance.activate(campaignKey, "Ashley");
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 5);
  }

  private static void validateActivateMethod(String settingsFile, ArrayList<UserExpectations.Variation> userVariation, VWOAdditionalParams additionalParams) throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(settingsFile, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(settingsFile).build();


    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.activate(campaignKey, TestUtils.getUsers()[i], additionalParams);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }
}
