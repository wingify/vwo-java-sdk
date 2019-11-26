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
import com.vwo.logger.Logger;
import com.vwo.models.Settings;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class FeatureEnabledTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(FeatureEnabledTests.class);

  @Test
  public void validationTests() throws IOException {
    LOGGER.info("Should return false if no campaignKey is passed");
    assertFalse(vwoInstance.isFeatureEnabled("", userId));

    LOGGER.info("Should return false if no userId is passed");
    Settings settings = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    assertFalse(vwoInstance.isFeatureEnabled(settings.getCampaigns().get(0).getKey(), ""));

    LOGGER.info("Should return false if campaignKey is not found in settingsFile");
    assertFalse(vwoInstance.isFeatureEnabled("NO_SUCH_CAMPAIGN_KEY", userId));
  }

  @Test
  public void abCampaignType() throws IOException {
    LOGGER.info("Should return false if campaign type is not feature rollout or feature test");

    Settings settingsFile1 = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String campaignKey = settingsFile1.getCampaigns().get(0).getKey();
    assertEquals(vwoInstance.isFeatureEnabled(campaignKey, TestUtils.getRandomUser()), false);
  }

  @Test
  public void featureRolloutTraffic0Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 0");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_0, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_0_WEIGHT_10_20_30_40);
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
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 1000");

    featureRolloutTests(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, UserExpectations.FEATURE_ROLLOUT_TEST_TRAFFIC_100_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureTestTraffic0Tests() throws IOException {
    LOGGER.info("Should test against a feature rollout campaign settings with traffic 0");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_0, UserExpectations.FEATURE_TEST_TRAFFIC_0_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureTestTraffic25Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 25");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_25, UserExpectations.FEATURE_TEST_TRAFFIC_25_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureTestTraffic50Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 50");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_50, UserExpectations.FEATURE_TEST_TRAFFIC_50_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureTestTraffic75Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 75");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_75, UserExpectations.FEATURE_TEST_TRAFFIC_75_WEIGHT_10_20_30_40);
  }

  @Test
  public void featureTestTraffic100Tests() throws IOException {
    LOGGER.info("Should test against a feature test campaign settings with traffic 1000");

    featureTestTests(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100, UserExpectations.FEATURE_TEST_TRAFFIC_100_WEIGHT_10_20_30_40);
  }

  private void featureRolloutTests(String SettingsConfig, ArrayList<UserExpectations.Variation> userExpectations) throws IOException {
    vwoInstance = VWO.launch(SettingsConfig).build();
    Settings featureRolloutSettingsConfig = new ObjectMapper().readValue(SettingsConfig, Settings.class);
    String campaignKey = featureRolloutSettingsConfig.getCampaigns().get(0).getKey();

    for (int i = 0; i < userExpectations.size(); i++) {
      boolean isFeatureEnabled = vwoInstance.isFeatureEnabled(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(isFeatureEnabled, userExpectations.get(i).getVariation() == null ? false : true);
    }
  }

  private void featureTestTests(String SettingsConfig, ArrayList<UserExpectations.FeatureEnabled> userExpectations) throws IOException {
    vwoInstance = VWO.launch(SettingsConfig).build();
    Settings featureTestSettingsConfig = new ObjectMapper().readValue(SettingsConfig, Settings.class);
    String campaignKey = featureTestSettingsConfig.getCampaigns().get(0).getKey();

    for (int i = 0; i < userExpectations.size(); i++) {
      boolean isFeatureEnabled = vwoInstance.isFeatureEnabled(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(isFeatureEnabled, userExpectations.get(i).getIsFeatureEnabled());
    }
  }
}
