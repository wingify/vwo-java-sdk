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

package com.vwo.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.models.Settings;
import com.vwo.logger.Logger;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GetVariationTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(GetVariationTests.class);


  @Test
  public void validationTests() throws IOException {
    LOGGER.info("Should return null if no campaignKey is passed");
    assertEquals(vwoInstance.getVariationName("", userId), null);

    LOGGER.info("Should return null if no userId is passed");
    Settings settings = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    assertEquals(vwoInstance.getVariationName(settings.getCampaigns().get(0).getKey(), ""), null);

    LOGGER.info("Should return null if campaignKey is not found in settingsFile");
    assertEquals(vwoInstance.getVariationName("NO_SUCH_CAMPAIGN_KEY", userId), null);
  }

  @Test
  public void setting1Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, UserExpectations.DEV_TEST_1);
  }

  @Test
  public void featureRolloutCampaignTypeTests() throws IOException {
    LOGGER.info("Should return null for feature rollout type campaigns");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();

    assertNull(vwoInstance.getVariationName(campaignKey, TestUtils.getRandomUser()));
  }

  @Test
  public void setting2Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50, UserExpectations.DEV_TEST_2);
  }

  @Test
  public void setting3Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:20-80");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_20_80, UserExpectations.DEV_TEST_3);
  }

  @Test
  public void setting4Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:20 and split:10-90");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_20_WEIGHT_10_90, UserExpectations.DEV_TEST_4);
  }

  @Test
  public void setting5Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:0-100");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_0_100, UserExpectations.DEV_TEST_5);
  }

  @Test
  public void setting6Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:33.3333:33.3333:33.3333");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.DEV_TEST_6);
  }

  @Test
  public void settingWith10VariationsTests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:75 with 10 variations of eqial weight");

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_75_VARIATIONS_10, UserExpectations.AB_TRAFFIC_75_VARIATIONS_10);
  }

  @Test
  public void notRunningCampaignTests() throws IOException {
    LOGGER.info("Should get null variation for non running campaign");

    getVariationTest(com.vwo.tests.data.Settings.AB_NOT_RUNNING_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_0_WEIGHT_10_20_30_40);
  }

  private void getVariationTest(String settingsFile, ArrayList<UserExpectations.Variation> userVariation) throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(settingsFile, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(settingsFile).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariationName(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }
}