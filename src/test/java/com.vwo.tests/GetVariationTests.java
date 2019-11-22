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
import com.vwo.logger.LoggerManager;
import com.vwo.models.SettingFileConfig;
import com.vwo.tests.data.Settings;
import com.vwo.tests.data.UserVariations;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetVariationTests {
  private static VWO vwoInstance = VWO.createInstance(Settings.settings1).build();
  private static String userId = TestUtils.getRandomUser();
  private static final LoggerManager LOGGER = LoggerManager.getLogger(GetVariationTests.class);


  @Test
  public void validationTests() throws IOException {
    LOGGER.info("Should return null if no campaignTestKey is passed");
    assertEquals(vwoInstance.getVariation("", userId), null);

    LOGGER.info("Should return null if no userId is passed");
    SettingFileConfig settingFileConfig = new ObjectMapper().readValue(Settings.settings1, SettingFileConfig.class);
    assertEquals(vwoInstance.getVariation(settingFileConfig.getCampaigns().get(0).getKey(), ""), null);

    LOGGER.info("Should return null if campaignTestKey is not found in settingsFile");
    assertEquals(vwoInstance.getVariation("NO_SUCH_CAMPAIGN_KEY", userId), null);
  }

  @Test
  public void setting1Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:50 and split:50-50");

    getVariationTest(Settings.settings1, UserVariations.DEV_TEST_1);
  }

  @Test
  public void setting2Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:50-50");

    getVariationTest(Settings.settings2, UserVariations.DEV_TEST_2);
  }

  @Test
  public void setting3Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:20-80");

    getVariationTest(Settings.settings3, UserVariations.DEV_TEST_3);
  }

  @Test
  public void setting4Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:20 and split:10-90");

    getVariationTest(Settings.settings4, UserVariations.DEV_TEST_4);
  }

  @Test
  public void setting5Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:0-100");

    getVariationTest(Settings.settings5, UserVariations.DEV_TEST_5);
  }

  @Test
  public void setting6Tests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:100 and split:33.3333:33.3333:33.3333");

    getVariationTest(Settings.settings6, UserVariations.DEV_TEST_6);
  }

  @Test
  public void settingWith10VariationsTests() throws IOException, NoSuchFieldException, IllegalAccessException {
    LOGGER.info("Should test against a campaign settings: traffic:75 with 10 variations of eqial weight");

    getVariationTest(Settings.settingsWith10Variations, UserVariations.TEN_Variations);
  }

  public void getVariationTest(String settingsFile, ArrayList<UserVariations.Variation> userVariation) throws IOException {
    SettingFileConfig settingsConfig = new ObjectMapper().readValue(settingsFile, SettingFileConfig.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.createInstance(settingsFile).build();

    for (int i = 0; i < userVariation.size(); i++) {
      String variationName = vwoInstance.getVariation(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
  }
}
