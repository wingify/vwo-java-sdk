package com.vwo.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.logger.LoggerManager;
import com.vwo.models.SettingFileConfig;
import com.vwo.tests.data.Settings;
import com.vwo.tests.data.UserVariations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TrackTests {
  private static VWO vwoInstance = VWO.createInstance(Settings.settings1).build();
  private static String userId = TestUtils.getRandomUser();
  private static final LoggerManager LOGGER = LoggerManager.getLogger(TrackTests.class);


  @Test
  public void validationTests() throws IOException {
    SettingFileConfig settingFileConfig = new ObjectMapper().readValue(Settings.settings1, SettingFileConfig.class);
    String goalIdentifier = settingFileConfig.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    String campaignTestKey = settingFileConfig.getCampaigns().get(0).getKey();

    LOGGER.info("Should return false if no campaignTestKey is passed");
    assertEquals(vwoInstance.track("", userId, goalIdentifier), false);

    LOGGER.info("Should return false if no userId is passed");
    assertEquals(vwoInstance.track(campaignTestKey, "", goalIdentifier), false);

    LOGGER.info("Should return false if no goalIdentifier is passed");
    assertEquals(vwoInstance.track(campaignTestKey, userId, ""), false);

    LOGGER.info("Should return false if campaignTestKey is not found in settingsFile");
    assertEquals(vwoInstance.track("NO_SUCH_CAMPAIGN_KEY", userId, goalIdentifier), false);

    LOGGER.info("Should return false if goalIdentifier is not found in settingsFile");
    assertEquals(vwoInstance.track(campaignTestKey, userId, "NO_SUCH_GOAL_IDENTIFIER"), false);
  }

  @Test
  public void setting1Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings1, SettingFileConfig.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_1").get(UserVariations.class);

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
  public void setting2TestsWithoutRevenue() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings2, SettingFileConfig.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings2).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_2").get(UserVariations.class);


    for (int i = 0; i < userVariation.size(); i++) {
      boolean isTracked = vwoInstance.track(campaignKey, TestUtils.getUsers()[i], goalIdentifier);
      assertEquals(isTracked, false);
    }
  }

  @Test
  public void setting2Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings2, SettingFileConfig.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings2).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_2").get(UserVariations.class);


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

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings3, SettingFileConfig.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings3).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_3").get(UserVariations.class);

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

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings4, SettingFileConfig.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings4).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_4").get(UserVariations.class);

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

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings5, SettingFileConfig.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings5).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_5").get(UserVariations.class);

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

    SettingFileConfig settingsFile = new ObjectMapper().readValue(Settings.settings6, SettingFileConfig.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings6).build();

    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    String goalIdentifier = settingsFile.getCampaigns().get(0).getGoals().get(0).getIdentifier();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_6").get(UserVariations.class);

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