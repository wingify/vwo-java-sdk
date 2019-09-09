package com.vwo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.logger.LoggerManager;
import com.vwo.models.SettingFileConfig;
import com.vwo.tests.data.Settings;
import com.vwo.tests.data.UserVariations;
import com.vwo.tests.utils.TestUtils;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;


public class ActivateTests {
  private static VWO vwoInstance = VWO.createInstance(Settings.settings1).build();
  private static String userId = TestUtils.getRandomUser();
  private static final LoggerManager LOGGER = LoggerManager.getLogger(ActivateTests.class);

  @Test
  public void validationTests() throws NoSuchMethodException, IOException {
    LOGGER.info("Should return null if no campaignTestKey is passed");
    assertEquals(vwoInstance.activate("", userId), null);

    LOGGER.info("Should return null if no userId is passed");
    SettingFileConfig settingFileConfig = new ObjectMapper().readValue(Settings.settings1, SettingFileConfig.class);
    assertEquals(vwoInstance.activate(settingFileConfig.getCampaigns().get(0).getKey(), ""), null);

    LOGGER.info("Should return null if campaignTestKey is not found in settingsFile");
    assertEquals(vwoInstance.activate("NO_SUCH_CAMPAIGN_KEY", userId), null);
  }

  @Test
  public void setting1Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    validateActivateMethod(Settings.settings1, UserVariations.DEV_TEST_1);
  }

  @Test
  public void setting2Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    validateActivateMethod(Settings.settings2, UserVariations.DEV_TEST_2);
  }

  @Test
  public void setting3Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:20-80");

    validateActivateMethod(Settings.settings3, UserVariations.DEV_TEST_3);
  }

  @Test
  public void setting4Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:20 and split:10-90");

    validateActivateMethod(Settings.settings4, UserVariations.DEV_TEST_4);
  }

  @Test
  public void setting5Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:0-100");

    validateActivateMethod(Settings.settings5, UserVariations.DEV_TEST_5);
  }

  @Test
  public void setting6Tests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:33.3333:33.3333:33.3333");

    validateActivateMethod(Settings.settings6, UserVariations.DEV_TEST_6);
  }

  @Test
  public void settingWith10VariationsTests() throws IOException {
    LOGGER.info("Should test against a campaign settings: traffic:75 with 10 variations of eqial weight");

    validateActivateMethod(Settings.settingsWith10Variations, UserVariations.TEN_Variations);
  }

  private static void validateActivateMethod(String settingsFile, ArrayList<UserVariations.Variation> userVariation) throws IOException {
    SettingFileConfig settingsConfig = new ObjectMapper().readValue(settingsFile, SettingFileConfig.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.createInstance(settingsFile).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.activate(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }
}