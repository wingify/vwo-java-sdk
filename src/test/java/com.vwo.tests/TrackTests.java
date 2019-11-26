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


public class TrackTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(TrackTests.class);


  @Test
  public void validationTests() throws IOException {
    Settings settings = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String goalIdentifier = settings.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    String campaignKey = settings.getCampaigns().get(0).getKey();

    LOGGER.info("Should return false if no campaignKey is passed");
    assertEquals(vwoInstance.track("", userId, goalIdentifier), false);

    LOGGER.info("Should return false if no userId is passed");
    assertEquals(vwoInstance.track(campaignKey, "", goalIdentifier), false);

    LOGGER.info("Should return false if no goalIdentifier is passed");
    assertEquals(vwoInstance.track(campaignKey, userId, ""), false);

    LOGGER.info("Should return false if campaignKey is not found in settingsFile");
    assertEquals(vwoInstance.track("NO_SUCH_CAMPAIGN_KEY", userId, goalIdentifier), false);

    LOGGER.info("Should return false if goalIdentifier is not found in settingsFile");
    assertEquals(vwoInstance.track(campaignKey, userId, "NO_SUCH_GOAL_IDENTIFIER"), false);
  }

  @Test
  public void setting1Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_1").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertEquals(isTracked, true);
      } else {
        assertEquals(isTracked, false);
      }
    }
  }

  @Test
  public void featureRolloutCampaignTypeTests() throws IOException {
    LOGGER.info("Should return false for feature rollout type campaigns");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();

    assertEquals(vwoInstance.track(campaignKey, TestUtils.getRandomUser(), "MY_GOAL"), false);
  }

  @Test
  public void setting2TestsWithoutRevenue() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_2").get(UserExpectations.class);


    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);
      assertEquals(isTracked, false);
    }
  }

  @Test
  public void setting2Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_2").get(UserExpectations.class);


    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier, 123);

      if (userVariation.get(i).getVariation() != null) {
        assertEquals(isTracked, true);
      } else {
        assertEquals(isTracked, false);
      }
    }
  }

  @Test
  public void setting3Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:20-80");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_20_80, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_20_80).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_3").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertEquals(isTracked, true);
      } else {
        assertEquals(isTracked, false);
      }
    }
  }

  @Test
  public void setting4Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:20 and split:10-90");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_20_WEIGHT_10_90, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_20_WEIGHT_10_90).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_4").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertEquals(isTracked, true);
      } else {
        assertEquals(isTracked, false);
      }
    }
  }

  @Test
  public void setting5Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:0-100");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_0_100, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_0_100).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_5").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertEquals(isTracked, true);
      } else {
        assertEquals(isTracked, false);
      }
    }
  }

  @Test
  public void setting6Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:33.3333:33.3333:33.3333");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("DEV_TEST_6").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertEquals(isTracked, true);
      } else {
        assertEquals(isTracked, false);
      }
    }
  }
}