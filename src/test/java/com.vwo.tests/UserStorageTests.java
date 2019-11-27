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
  public void getMethodForNonMatchingMapTests() throws IOException {
    LOGGER.info("Should ignore user storage's variation and find variation using murmur logic if non matching map found (user id or campaign key is different)");

    Storage.User userStorage = this.getUserStorageWithNonMatchingUser();
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

    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.DEV_TEST_6, userStorage);
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_VARIATIONS_10, UserExpectations.DEV_TEST_6, userStorage);
  }

  @Test
  public void setMethodThrowErrorTests() throws IOException {
    LOGGER.info("Should ignore saved variations if getting any exception from customer defined function");

    Storage.User userStorage = this.getUserStorageWithExceptions();
    getVariationTest(com.vwo.tests.data.Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, UserExpectations.DEV_TEST_6, userStorage);
  }


  private void getVariationTest(String settingsFile, ArrayList<UserExpectations.Variation> userVariation, Storage.User userStorage) throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(settingsFile, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(settingsFile).withUserStorage(userStorage).build();

    for (int i = 0; i < userVariation.size(); i++) {
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

  private Storage.User getUserStorageWithNonMatchingUser() {
    return new Storage.User() {
      @Override
      public Map<String, String> get(String userId, String campaignName) throws Exception {
        return new HashMap<String, String>(){{
          put("userId", "punit");
          put("campaignKey", "DEV_TEST_6");
          put("variationName", "Variation-1");
        }};
      }

      @Override
      public void set(Map<String, String> map) {
        // Not required for the case.
      }
    };
  }

  private void prefillUserStorageMap(ArrayList<Map<String, String>> campaignStorageArray,  ArrayList<UserExpectations.Variation>userVariation) {
    for (int i = 0; i < userVariation.size(); i++) {
      String user = userVariation.get(i).getUser();
      String variation = userVariation.get(i).getVariation();
      campaignStorageArray.add(new HashMap<String, String>(){{
        put("userId", user);
        put("campaignKey", "DEV_TEST_6");
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
        put("campaignKey", "DEV_TEST_6");
        put("missingVariationName", variation);
      }});
    }
  }
}