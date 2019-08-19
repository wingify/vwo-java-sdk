package com.vwo.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.models.SettingFileConfig;
import com.vwo.tests.data.Settings;
import com.vwo.tests.data.UserVariations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetVariationTests {
  public static VWO vwoInstance = VWO.createInstance(Settings.settings1).build();
  public static String userId = TestUtils.getRandomUser();
  public static SettingFileConfig settingFileConfig;

  @Test
  public void validationTests() throws NoSuchMethodException, IOException {
    System.out.println("Should return null if no campaignTestKey is passed");
    assertEquals(vwoInstance.getVariation("", userId), null);

    System.out.println("Should return null if no userId is passed");
    SettingFileConfig settingFileConfig = new ObjectMapper().readValue(Settings.settings1, SettingFileConfig.class);
    assertEquals(vwoInstance.getVariation(settingFileConfig.getCampaigns().get(0).getKey(), ""), null);

    System.out.println("Should return null if campaignTestKey is not found in settingsFile");
    assertEquals(vwoInstance.getVariation("NO_SUCH_CAMPAIGN_KEY", userId), null);
  }

  @Test
  public void setting1Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    System.out.println("Should test against a campaign settings: traffic:50 and split:50-50");

    SettingFileConfig settingsFile1 = new ObjectMapper().readValue(Settings.settings1, SettingFileConfig.class);
    String campaignKey = settingsFile1.getCampaigns().get(0).getKey();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_1").get(UserVariations.class);

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }

    
  }

  @Test
  public void setting2Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    System.out.println("Should test against a campaign settings: traffic:100 and split:50-50");

    SettingFileConfig settingsFile2 = new ObjectMapper().readValue(Settings.settings2, SettingFileConfig.class);
    String campaignKey = settingsFile2.getCampaigns().get(0).getKey();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_2").get(UserVariations.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings2).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }

  @Test
  public void setting3Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    System.out.println("Should test against a campaign settings: traffic:100 and split:20-80");

    SettingFileConfig settingsFile3 = new ObjectMapper().readValue(Settings.settings3, SettingFileConfig.class);
    String campaignKey = settingsFile3.getCampaigns().get(0).getKey();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_3").get(UserVariations.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings3).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }

  @Test
  public void setting4Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    System.out.println("Should test against a campaign settings: traffic:20 and split:10-90");

    SettingFileConfig settingsFile4 = new ObjectMapper().readValue(Settings.settings4, SettingFileConfig.class);
    String campaignKey = settingsFile4.getCampaigns().get(0).getKey();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_4").get(UserVariations.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings4).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }

  @Test
  public void setting5Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    System.out.println("Should test against a campaign settings: traffic:100 and split:0-100");

    SettingFileConfig settingsFile5 = new ObjectMapper().readValue(Settings.settings5, SettingFileConfig.class);
    String campaignKey = settingsFile5.getCampaigns().get(0).getKey();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_5").get(UserVariations.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings5).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }

  @Test
  public void setting6Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    System.out.println("Should test against a campaign settings: traffic:100 and split:33.3333:33.3333:33.3333");

    SettingFileConfig settingsFile6 = new ObjectMapper().readValue(Settings.settings6, SettingFileConfig.class);
    String campaignKey = settingsFile6.getCampaigns().get(0).getKey();
    ArrayList<UserVariations.Variation> userVariation = (ArrayList<UserVariations.Variation>) UserVariations.class.getField("DEV_TEST_6").get(UserVariations.class);
    VWO vwoInstance = VWO.createInstance(Settings.settings6).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }
}