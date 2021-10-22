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
import com.vwo.models.response.BatchEventData;
import com.vwo.models.response.Settings;
import com.vwo.services.storage.Storage;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import com.vwo.utils.StorageUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;


public class UserStorageTests {
  private static final Logger LOGGER = Logger.getLogger(UserStorageTests.class);
  BatchEventData batchEventData = new BatchEventData();

  @Test
  public void validMapValidationTest() {
    LOGGER.info("Should validate correct user storage service map");

    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    this.prefillUserStorageMap(campaignStorageArray, UserExpectations.AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10);

    for (Map<String, String> campaignStorage: campaignStorageArray) {
      assertTrue(StorageUtils.isValidUserStorageMap(campaignStorage));
    }
  }

  @Test
  public void invalidMapValidationTest() {
    LOGGER.info("Should invalidate incorrect user storage service map");

    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();

    LOGGER.info("Missing User ID validation");
    campaignStorageArray.add(new HashMap<String, String>(){{
      put("abc", "punit");
      put("campaignKey", "my_key");
      put("variationName", "Variation-1");
    }});

    LOGGER.info("Missing Campaign Test key validation");
    campaignStorageArray.add(new HashMap<String, String>(){{
      put("userId", "punit");
      put("abcd", "my_key");
      put("variationName", "Variation-1");
    }});

    LOGGER.info("Missing variation name validation");
    campaignStorageArray.add(new HashMap<String, String>(){{
      put("userId", "punit");
      put("campaignKey", "my_key");
      put("abc", "Variation-1");
    }});

    for (Map<String, String> campaignStorage: campaignStorageArray) {
      assertFalse(StorageUtils.isValidUserStorageMap(campaignStorage));
    }
  }

  @Test
  public void getMethodTests() throws IOException {
    LOGGER.info("Should get the already saved variations");

    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    this.prefillUserStorageMap(campaignStorageArray, UserExpectations.AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10);

    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_VARIATIONS_10, UserExpectations.AB_USER_STORAGE_TRAFFIC_100_VARIATIONS_10, userStorage);
  }

  @Test
  public void getMethodForNonExistingVariationTests() throws IOException {
    LOGGER.info("Should ignore user storage's saved variation and find via murmur logic if not found in settings file");

    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    this.prefillUserStorageMap(campaignStorageArray, UserExpectations.AB_TRAFFIC_100_INVALID_VARIATIONS_10);

    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_75_VARIATIONS_10, UserExpectations.AB_TRAFFIC_75_VARIATIONS_10, userStorage);
  }

  @Test
  public void getMethodForInvalidMapTests() throws IOException {
     LOGGER.info("Should ignore user storage and find variation using murmur logic if invalid map found");


    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    this.prefillInvalidUserStorageMap(campaignStorageArray, UserExpectations.AB_TRAFFIC_100_INVALID_VARIATIONS_10);

    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_75_VARIATIONS_10, UserExpectations.AB_TRAFFIC_75_VARIATIONS_10, userStorage);
  }

  @Test
  public void getMethodThrowErrorTests() throws IOException {
    LOGGER.info("Should ignore user storage and find variation using murmur logic if getting any exception from customer defined functions");

    Storage.User userStorage = this.getUserStorageWithExceptions();
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_75_VARIATIONS_10, UserExpectations.AB_TRAFFIC_75_VARIATIONS_10, userStorage);
  }

  @Test
  public void setMethodTests() throws IOException {
    LOGGER.info("Should save variations corresponding to a settings file with 3 variations and loopkup same for settings with 10 variations");

    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    Storage.User userStorage = this.getUserStorage(campaignStorageArray);

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.AB_TRAFFIC_100_WEIGHT_33_33_33, userStorage);
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_VARIATIONS_10, UserExpectations.AB_TRAFFIC_100_WEIGHT_33_33_33, userStorage);
  }

  @Test
  public void setMethodThrowErrorTests() throws IOException {
    LOGGER.info("Should ignore saved variations if getting any exception from customer defined function");

    Storage.User userStorage = this.getUserStorageWithExceptions();
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.AB_TRAFFIC_100_WEIGHT_33_33_33, userStorage);
  }

  @Test
  public void apisCalledBeforeActivateFalsyTests() throws IOException {
    LOGGER.info("Should return null if getVariation/track API is called before activate API");

    Storage.User userStorage = this.getUserStorage(new ArrayList<Map<String, String>>());
    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withDevelopmentMode(true).withUserStorage(userStorage).build();
    assertEquals(vwoInstance.getVariationName(campaignKey, "Ashley"), null);
    assertEquals(vwoInstance.track(campaignKey, "Ashley", "CUSTOM").get(campaignKey), false);

    LOGGER.info("Should return false if getFeatureVariableValue/track API is called before isFeatureEnabled API for feature test");
    settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100, Settings.class);
    campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100).withDevelopmentMode(true).withUserStorage(userStorage).build();
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, "STRING_VARIABLE", "Ashley"), null);
    assertEquals(vwoInstance.track(campaignKey, "Ashley", "FEATURE_TEST_GOAL").get(campaignKey), false);

    LOGGER.info("Should return null if getFeatureVariableValue/track API is called before isFeatureEnabled API for feature rollout");
    settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).withDevelopmentMode(true).withUserStorage(userStorage).build();
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, "STRING_VARIABLE", "Ashley"), null);
  }

  @Test
  public void apisCalledBeforeActivateTruthyTests() throws IOException {
    LOGGER.info("Should return variation if getVariation/track API is called after activate API");

    Storage.User userStorage = this.getUserStorage(new ArrayList<Map<String, String>>());
    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withDevelopmentMode(true).withUserStorage(userStorage).build();
    assertEquals(vwoInstance.activate(campaignKey, "Ashley"), "Variation-1");
    assertEquals(vwoInstance.getVariationName(campaignKey, "Ashley"), "Variation-1");
    assertEquals(vwoInstance.track(campaignKey, "Ashley", "CUSTOM").get(campaignKey), true);

    LOGGER.info("Should return false if getFeatureVariableValue/track API is called before isFeatureEnabled API for feature test");
    settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100, Settings.class);
    campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_TEST_TRAFFIC_100).withDevelopmentMode(true).withUserStorage(userStorage).build();
    assertEquals(vwoInstance.isFeatureEnabled(campaignKey, "Ashley"), true);
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, "STRING_VARIABLE", "Ashley"), "Variation-2 string");
    assertEquals(vwoInstance.track(campaignKey, "Ashley", "FEATURE_TEST_GOAL").get(campaignKey), true);

    LOGGER.info("Should return value if getFeatureVariableValue/track API is called after isFeatureEnabled API for feature rollout");
    settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
    campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    vwoInstance = VWO.launch(com.vwo.tests.data.Settings.FEATURE_ROLLOUT_TRAFFIC_100).withDevelopmentMode(true).withUserStorage(userStorage).build();
    assertEquals(vwoInstance.isFeatureEnabled(campaignKey, "Ashley"), true);
    assertEquals(vwoInstance.getFeatureVariableValue(campaignKey, "STRING_VARIABLE", "Ashley"), "this_is_a_string");
  }

  @Test
  public void returningUserFlagPassedGloballyTest() throws IOException {
    LOGGER.info("should be/not added to eventBatching queue depending upon the flag passed while launching VWO.");
    Storage.User userStorage = this.getUserStorage(new ArrayList<Map<String, String>>());
    batchEventData.setEventsPerRequest(50);

    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();

    //when the global value is not passed, by default false should be used.
    userStorage = this.getUserStorage(new ArrayList<Map<String, String>>());
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withUserStorage(userStorage).withBatchEvents(batchEventData).build();
    vwoInstance.activate(campaignKey, "Ashley");
    vwoInstance.activate(campaignKey, "Ashley");
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
  }

  @Test
  public void returningUserFlagPassedLocalTest() throws IOException {
    LOGGER.info("should return variation track user call should be sent if userStorageService is not passed");

    ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();
    Storage.User userStorage = this.getUserStorage(campaignStorageArray);
    batchEventData.setEventsPerRequest(50);

    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withUserStorage(userStorage).withBatchEvents(batchEventData).build();

    VWOAdditionalParams params = new VWOAdditionalParams();
    vwoInstance.activate(campaignKey, "Ashley", params);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);
    vwoInstance.activate(campaignKey, "Ashley", params);
    assertEquals(vwoInstance.getBatchEventQueue().getBatchQueue().size(), 1);

  }


  private void getVariationTest(String settingsFile, ArrayList<UserExpectations.Variation> userVariation, Storage.User userStorage) throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(settingsFile, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(settingsFile).withUserStorage(userStorage).build();

    for (int i = 0; i < userVariation.size(); i++) {
      vwoInstance.activate(campaignKey, TestUtils.getUsers()[i]);
      String variationName = vwoInstance.getVariationName(campaignKey, TestUtils.getUsers()[i]);
      assertEquals(variationName, userVariation.get(i).getVariation());
    }
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

  private Storage.User getUserStorageWithExceptions() {
    return new Storage.User() {
      @Override
      public Map<String, String> get(String userId, String campaignName) throws Exception {
        throw new Exception();
      }

      @Override
      public void set(Map<String, String> map) throws Exception {
        throw new Exception();
      }
    };
  }

  private void prefillUserStorageMap(ArrayList<Map<String, String>> campaignStorageArray,  ArrayList<UserExpectations.Variation>userVariation) {
    for (int i = 0; i < userVariation.size(); i++) {
      String user = userVariation.get(i).getUser();
      String variation = userVariation.get(i).getVariation();
      campaignStorageArray.add(new HashMap<String, String>(){{
        put("userId", user);
        put("campaignKey", "AB_TRAFFIC_100_WEIGHT_33_33_33");
        put("variationName", variation);
      }});
    }
  }

  private void prefillInvalidUserStorageMap(ArrayList<Map<String, String>> campaignStorageArray,  ArrayList<UserExpectations.Variation>userVariation) {
    for (int i = 0; i < userVariation.size(); i++) {
      String user = userVariation.get(i).getUser();
      String variation = userVariation.get(i).getVariation();
      campaignStorageArray.add(new HashMap<String, String>(){{
        put("userId", user);
        put("campaignKey", "AB_TRAFFIC_100_WEIGHT_33_33_33");
        put("missingVariationName", variation);
      }});
    }
  }
}
