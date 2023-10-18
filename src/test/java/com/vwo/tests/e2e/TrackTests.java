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

package com.vwo.tests.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.VWOAdditionalParams;
import com.vwo.enums.GoalEnums;
import com.vwo.logger.Logger;
import com.vwo.models.response.Settings;
import com.vwo.services.storage.Storage;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TrackTests {
  private static VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
  private static String userId = TestUtils.getRandomUser();
  private static final Logger LOGGER = Logger.getLogger(TrackTests.class);


  @Test
  public void validationTests() throws IOException {
    Settings settings = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String goalIdentifier = settings.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    String campaignKey = settings.getCampaigns().get(0).getKey();

    LOGGER.info("Should return null if invalid campaignKey is passed");
    assertNull(vwoInstance.track("", userId, goalIdentifier));
    assertNull(vwoInstance.track(true, userId, goalIdentifier));
    assertNull(vwoInstance.track(12345, userId, goalIdentifier));
    assertNull(vwoInstance.track(0.5345, userId, goalIdentifier));
    assertNull(vwoInstance.track(new HashMap<String, String>(), userId, goalIdentifier));
    assertNull(vwoInstance.track(new ArrayList<String>(), userId, goalIdentifier));
    assertNull(vwoInstance.track(new String[0], userId, goalIdentifier));

    LOGGER.info("Should return null if no userId is passed");
    assertNull(vwoInstance.track(campaignKey, "", goalIdentifier));

    LOGGER.info("Should return null if no goalIdentifier is passed");
    assertNull(vwoInstance.track(campaignKey, userId, ""));

    LOGGER.info("Should return false if campaignKey is not found in settingsFile");
    assertFalse(vwoInstance.track("NO_SUCH_CAMPAIGN_KEY", userId, goalIdentifier).get("NO_SUCH_CAMPAIGN_KEY"));

    LOGGER.info("Should return false if goalIdentifier is not found in settingsFile");
    assertFalse(vwoInstance.track(campaignKey, userId, "NO_SUCH_GOAL_IDENTIFIER").get(campaignKey));
  }

  @Test
  public void setting1Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_50_WEIGHT_50_50").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertTrue(campaignStatus.get(campaignKey));
      } else {
        assertFalse(campaignStatus.get(campaignKey));
      }
    }
  }

  @Test
  public void featureRolloutCampaignTypeTests() throws IOException {
    LOGGER.info("Should return null for feature rollout type campaigns");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();

    assertFalse(vwoInstance.track(campaignKey, TestUtils.getRandomUser(), "MY_GOAL").get(campaignKey));
  }

  @Test
  public void setting2TestsWithoutRevenue() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);


    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);
      assertFalse(campaignStatus.get(campaignKey));
    }
  }

  @Test
  public void setting2Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50, Settings.class);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_50_50).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);


    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier, new VWO.AdditionalParams().setRevenueValue(123));

      if (userVariation.get(i).getVariation() != null) {
        assertTrue(campaignStatus.get(campaignKey));
      } else {
        assertFalse(campaignStatus.get(campaignKey));
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
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_20_80").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertTrue(campaignStatus.get(campaignKey));
      } else {
        assertFalse(campaignStatus.get(campaignKey));
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
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_20_WEIGHT_10_90").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertTrue(campaignStatus.get(campaignKey));
      } else {
        assertFalse(campaignStatus.get(campaignKey));
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
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_0_100").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertTrue(campaignStatus.get(campaignKey));
      } else {
        assertFalse(campaignStatus.get(campaignKey));
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
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_33_33_33").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      Map<String, Boolean> campaignStatus = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);

      if (userVariation.get(i).getVariation() != null) {
        assertTrue(campaignStatus.get(campaignKey));
      } else {
        assertFalse(campaignStatus.get(campaignKey));
      }
    }
  }

  @Test
  public void globalGoalWithNullCampaignTruthyTest() throws NoSuchFieldException, IllegalAccessException {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100).build();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked = vwoInstance.track(null, new TestUtils().getUsers()[i], "track1", additionalParams);
      Iterator iterator = isGoalTracked.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator.next();
        assertTrue((Boolean) pair.getValue());
      }

      Map<String, Boolean> isGoalTracked1 = vwoInstance.track(null, new TestUtils().getUsers()[i], "track2", additionalParams);
      Iterator iterator1 = isGoalTracked1.entrySet().iterator();
      while (iterator1.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator1.next();
        assertTrue((Boolean) pair.getValue());
      }

      Map<String, Boolean> isGoalTracked2 = vwoInstance.track(null, new TestUtils().getUsers()[i], "track4", additionalParams);
      Iterator iterator2 = isGoalTracked2.entrySet().iterator();
      while (iterator2.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator2.next();
        assertTrue((Boolean) pair.getValue());
      }
    }
  }

  @Test
  public void globalGoalWithNullCampaignFalsyTest() throws NoSuchFieldException, IllegalAccessException {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100).build();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);

    //pass the wrong goalIdentifier
    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked = vwoInstance.track(null, new TestUtils().getUsers()[i], "something");
      assertNull(isGoalTracked);
    }
  }

  @Test
  public void globalGoalWithCampaignArrayTruthyTest() throws NoSuchFieldException, IllegalAccessException {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100).build();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);

    String[] campaignList = {
      vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey(),
      vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).getKey(),
    };

    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track2", additionalParams);
      Iterator iterator = isGoalTracked.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator.next();
        assertTrue((Boolean) pair.getValue());
      }
    }
  }

  @Test
  public void globalGoalWithCampaignArrayFalsyTest() throws NoSuchFieldException, IllegalAccessException {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100).build();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);

    String[] campaignList = {
      "campaign1",
      "campaign2",
    };

    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track1", additionalParams);
      Iterator iterator = isGoalTracked.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator.next();
        assertFalse((Boolean) pair.getValue());
      }
    }
  }

  @Test
  public void globalGoalWithFalseGoalTest() throws NoSuchFieldException, IllegalAccessException {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100).build();
    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);

    String[] campaignList = {
      vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey(),
      vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).getKey(),
    };

    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked = vwoInstance.track(null, new TestUtils().getUsers()[i], "goalIdentifier", additionalParams);
      assertNull(isGoalTracked);

      Map<String, Boolean> isGoalTracked2 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "goalIdentifier", additionalParams);
      Iterator iterator = isGoalTracked2.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator.next();
        assertFalse((Boolean) pair.getValue());
      }
    }
  }

  @Test
  public void trackOnlyParticularGoalTypeTest() throws NoSuchFieldException, IllegalAccessException {
    VWO vwoInstance = VWO
      .launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100)
      .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
      .build();

    ArrayList<UserExpectations.Variation> userVariation = (ArrayList<UserExpectations.Variation>) UserExpectations.class.getField("AB_TRAFFIC_100_WEIGHT_50_50").get(UserExpectations.class);

    String[] campaignList = {
      vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey(),
      vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).getKey(),
    };

    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked = vwoInstance.track(null, new TestUtils().getUsers()[i], "track1", additionalParams);
      Iterator iterator = isGoalTracked.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator.next();
        assertTrue((Boolean) pair.getValue());
      }
    }

    //    track only custom goal
    vwoInstance = VWO
      .launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100)
      .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.CUSTOM)
      .build();
    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked2 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track1", additionalParams);
      assertTrue(isGoalTracked2.get(campaignList[0]));
      assertTrue(isGoalTracked2.get(campaignList[1]));
    }

    //track only revenue goal
    vwoInstance = VWO
      .launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100)
      .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.REVENUE)
      .build();
    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      Map<String, Boolean> isGoalTracked3 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track4", additionalParams);
      assertTrue(isGoalTracked3.get(campaignList[0]));
      assertTrue(isGoalTracked3.get(campaignList[1]));

      //track only revenue goal with no revenue value
      Map<String, Boolean> isGoalTracked4 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track4");
      assertFalse(isGoalTracked4.get(campaignList[0]));
      assertFalse(isGoalTracked4.get(campaignList[1]));
    }

    //track only custom goal passed from options in track campaign
    vwoInstance = VWO
      .launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100)
      .withGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL)
      .build();
    for (int i = 0; i < userVariation.size(); i++) {
      VWOAdditionalParams additionalParams = new VWOAdditionalParams();
      additionalParams.setRevenueValue(13);
      additionalParams.setGoalTypeToTrack(GoalEnums.GOAL_TYPES.CUSTOM);
      Map<String, Boolean> isGoalTracked5 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track2", additionalParams);
      assertTrue(isGoalTracked5.get(campaignList[0]));
      assertFalse(isGoalTracked5.get(campaignList[1]));

      //track only revenue goal
      additionalParams.setGoalTypeToTrack(GoalEnums.GOAL_TYPES.REVENUE);
      Map<String, Boolean> isGoalTracked6 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track3", additionalParams);
      assertTrue(isGoalTracked6.get(campaignList[0]));
      assertFalse(isGoalTracked6.get(campaignList[1]));

      //track all type of goals
      additionalParams.setGoalTypeToTrack(GoalEnums.GOAL_TYPES.ALL);
      Map<String, Boolean> isGoalTracked7 = vwoInstance.track(campaignList, new TestUtils().getUsers()[i], "track3", additionalParams);
      assertTrue(isGoalTracked7.get(campaignList[0]));
      assertTrue(isGoalTracked7.get(campaignList[1]));
    }
  }

  @Test
  public void testRevenuePropInEventArch() {
    vwoInstance = VWO
      .launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_EVENT_ARCH)
      .build();
    VWOAdditionalParams params = new VWOAdditionalParams();
    params.setRevenueValue(20);
    params.setEventProperties(new HashMap<>());
    Map<String, Boolean> trackResponse = vwoInstance.track(null, "Ashley", "track3", params);
    assertEquals(trackResponse.size(), 3);
  }

  private Storage.User getUserStorage(ArrayList<Map<String, String>> campaignStorageArray) {
    return new Storage.User() {
      @Override
      public Map<String, String> get(String userId, String campaignName) {
        for (Map<String, String> savedCampaign: campaignStorageArray) {
          if (savedCampaign.get("userId").equals(userId) && savedCampaign.get("campaignKey").equals(campaignName)) {
            return savedCampaign;
          }
        }
        return null;
      }

      @Override
      public void set(Map<String, String> map){
        campaignStorageArray.add(map);
      }
    };
  }

  @Test
  public void trackMCATest() throws IOException {//copy
    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_MCA, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    Map<String, String> userStorageMap = new HashMap<String, String>(){{
      put("userId", "Ashley");
      put("campaignKey", campaignKey);
      put("variationName", "Variation-1");
      put("goalIdentifier", "track2");
    }};
    campaignStorageArray.add(userStorageMap);
    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_MCA).withDevelopmentMode(true).withUserStorage(userStorage).build();

    VWOAdditionalParams params = new VWOAdditionalParams();
    params.setRevenueValue(20);

    assertEquals(vwoInstance.track(campaignKey, "Ashley", "track2", params).get(campaignKey), true);
  }

  @Test
  public void trackGoal_HasPropsTest() throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_HAS_PROPS, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    Map<String, String> userStorageMap = new HashMap<String, String>(){{
      put("userId", "Ashley");
      put("campaignKey", campaignKey);
      put("variationName", "Variation-1");
      put("goalIdentifier", "track2");
    }};
    campaignStorageArray.add(userStorageMap);
    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_HAS_PROPS).withDevelopmentMode(true).withUserStorage(userStorage).build();

    assertEquals(vwoInstance.track(campaignKey, "Ashley", "track2").get(campaignKey), true);
  }

  @Test
  public void trackGoal_noHasPropsTest() throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_HAS_PROPS, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    Map<String, String> userStorageMap = new HashMap<String, String>(){{
      put("userId", "Ashley");
      put("campaignKey", campaignKey);
      put("variationName", "Variation-1");
      put("goalIdentifier", "track1");
    }};
    campaignStorageArray.add(userStorageMap);
    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_AND_FT_TRAFFIC_100_HAS_PROPS).withDevelopmentMode(true).withUserStorage(userStorage).build();

    assertEquals(vwoInstance.track(campaignKey, "Ashley", "track1").get(campaignKey), false);
  }

  @Test
  public void trackEventProps_noRevenueValueInRevenueGoal() {
    String campaignKey = "track1";
    String goalIdentifier = "track3";
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_100_EVENT_PROPERTIES).withDevelopmentMode(true).build();
    VWOAdditionalParams params = new VWOAdditionalParams();
    HashMap<String, String> eventProperties = new HashMap<>();
    params.setEventProperties(eventProperties);
    assertEquals(vwoInstance.track(campaignKey, "Ashley", goalIdentifier, params).get(campaignKey), false);
  }

  @Test
  public void trackEventProps_noRevenueValueInNumberGoal() {
    String campaignKey = "track1";
    String goalIdentifier = "track4";
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_100_EVENT_PROPERTIES).withDevelopmentMode(true).build();
    VWOAdditionalParams params = new VWOAdditionalParams();
    HashMap<String, String> eventProperties = new HashMap<>();
    params.setEventProperties(eventProperties);
    assertEquals(vwoInstance.track(campaignKey, "Ashley", goalIdentifier, params).get(campaignKey), true);
  }

  @Test
  public void trackEventProps_eventPropertiesInsteadOfRevenueValueInRevenueGoal() {
    String campaignKey = "track1";
    String goalIdentifier = "track3";
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_100_EVENT_PROPERTIES).withDevelopmentMode(true).build();
    VWOAdditionalParams params = new VWOAdditionalParams();
    HashMap<String, Integer> eventProperties = new HashMap<>();
    eventProperties.put("abcd", 400);
    params.setEventProperties(eventProperties);
    assertEquals(vwoInstance.track(campaignKey, "Ashley", goalIdentifier, params).get(campaignKey), true);
  }

  @Test
  public void trackEventProps_wrongEventPropertiesInsteadOfRevenueValueInRevenueGoal() {
    String campaignKey = "track1";
    String goalIdentifier = "track3";
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_100_EVENT_PROPERTIES).withDevelopmentMode(true).build();
    VWOAdditionalParams params = new VWOAdditionalParams();
    HashMap<String, Integer> eventProperties = new HashMap<>();
    eventProperties.put("ab", 400);
    params.setEventProperties(eventProperties);
    assertEquals(vwoInstance.track(campaignKey, "Ashley", goalIdentifier, params).get(campaignKey), false);
  }
}
