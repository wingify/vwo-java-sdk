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

import com.vwo.VWO;
import com.vwo.VWOAdditionalParams;
import com.vwo.logger.Logger;
import com.vwo.logger.VWOLogger;
import com.vwo.services.storage.Storage;
import com.vwo.tests.data.Settings;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MEGTests {
  private static final Logger LOGGER = Logger.getLogger(MEGTests.class);
  private static VWO vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withCustomLogger(getCustomLogger()).withDevelopmentMode(true).build();
  ArrayList<Map<String, String>> campaignStorageArray = new ArrayList<>();

  @Test
  public void WhitelistingPassedForCalledCampaign() {
    LOGGER.debug("should return a variation as whitelisting is satisfied for the called campaign");
    String calledCampaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    String otherCampaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(3).getKey();
    Map<String, Object> whitelistingOptions = new HashMap<String, Object>() {
      {
        put("chrome", false);
      }
    };

    VWOAdditionalParams params = new VWOAdditionalParams();
    params.setVariationTargetingVariables(whitelistingOptions);
    //variation should be returned for the called campaign
    String variationName = vwoInstance.activate(calledCampaignKey, "Ashley", params);
    assertEquals(variationName, "Variation-1");
    //no variation should be assigned to the other campaign because whitelisting is satisfied for the previous campaign
    variationName = vwoInstance.activate(otherCampaignKey, "Ashley", params);
    assertEquals(variationName, null);
  }

  @Test
  public void whitelistingFailedForCalledCampaign() {
    LOGGER.debug("should return null as other campaign satisfies the whitelisting");
    String calledCampaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(3).getKey();
    String otherCampaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    Map<String, Object> whitelistingOptions = new HashMap<String, Object>() {
      {
        put("chrome", false);
      }
    };
    VWOAdditionalParams params = new VWOAdditionalParams();
    params.setVariationTargetingVariables(whitelistingOptions);
    //variation should be returned for the called campaign
    String variationName = vwoInstance.activate(calledCampaignKey, "Ashley", params);
    assertEquals(variationName, null);
    variationName = vwoInstance.activate(otherCampaignKey, "Ashley", params);
    assertEquals(variationName, "Variation-1");
  }

  @Test
  public void storagePassedForCalledCampaign() {
    LOGGER.debug("should return variation as storage is satisfied for the called campaign");
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withUserStorage(getUserStorage(campaignStorageArray)).withDevelopmentMode(true).build();
    String campaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    String otherCampaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(3).getKey();
    campaignStorageArray.clear();

    assertEquals(campaignStorageArray.size(), 0);
    String variation = vwoInstance.activate(campaignKey, "Ashley");
    assertEquals(variation, "Control");
    assertEquals(campaignStorageArray.size(), 1);

    variation = vwoInstance.activate(campaignKey, "Ashley");
    String variationName = vwoInstance.getVariationName(campaignKey, "Ashley");
    assertEquals(variation, "Control");
    assertEquals(variationName, "Control");
    assertEquals(campaignStorageArray.size(), 1);


    Map<String, Boolean> isGoalTracked = vwoInstance.track(campaignKey, "Ashley", "CUSTOM");
    assertEquals(isGoalTracked.get(campaignKey), true);
    assertTrue(campaignStorageArray.get(0).get("goalIdentifier").contains("CUSTOM"));
    assertEquals(campaignStorageArray.get(0).get("campaignKey"), campaignKey);

    //now since one of the campaign is already present in the storage. Calling for other campaigns would return variation as null.
    variation = vwoInstance.activate(otherCampaignKey, "Ashley");
    assertEquals(variation, null);
    variationName = vwoInstance.getVariationName(otherCampaignKey, "Ashley");
    assertEquals(variationName, null);
    isGoalTracked.clear();
    isGoalTracked = vwoInstance.track(otherCampaignKey, "Ashley", "CUSTOM");
    assertEquals(isGoalTracked.get(otherCampaignKey), false);
  }

  @Test
  public void storageFailedForCalledCampaign() {
    LOGGER.debug("should return null as other campaign satisfies the storage");
    campaignStorageArray.clear();
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withUserStorage(getUserStorage(campaignStorageArray)).withDevelopmentMode(true).build();
    String campaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(3).getKey();
    String otherCampaignKey = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();

    assertEquals(campaignStorageArray.size(), 0);
    String variation = vwoInstance.activate(otherCampaignKey, "Ashley");
    Map<String, Boolean> isGoalTracked = vwoInstance.track(otherCampaignKey, "Ashley", "CUSTOM");
    assertEquals(isGoalTracked.get(otherCampaignKey), true);
    assertEquals(variation, "Control");


    variation = vwoInstance.activate(campaignKey, "Ashley");
    assertEquals(variation, null);
    String variationName = vwoInstance.getVariationName(campaignKey, "Ashley");
    assertEquals(variationName, null);
    isGoalTracked.clear();
    isGoalTracked = vwoInstance.track(campaignKey, "Ashley", "CUSTOM");
    assertEquals(isGoalTracked.get(campaignKey), false);
  }

  @Test
  public void campaignNotPartOFGroup() {
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withDevelopmentMode(true).build();
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(4).getKey();

    // called campaign does not become part of MEG, variation null be assigned since campaign traffic is 10%
    String variation = vwoInstance.activate(calledCampaign, "Ashley");
    String variationName = vwoInstance.getVariationName(calledCampaign, "Ashley");
    Map<String, Boolean> isGoalTracked = vwoInstance.track(calledCampaign, "Ashley", "CUSTOM");
    assertNull(variation);
    assertNull(variationName);
    assertEquals(isGoalTracked.get(calledCampaign), false);
  }

  @Test
  public void preSegmentationFailed() {
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey();

    Map<String, String> customVariables = new HashMap<String, String>() {{
      put("browser", "chrome");
    }};

    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setSegments(getSegmentData("chrome", "false"));
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setSegments(getSegmentData("chrome", "false"));

    VWOAdditionalParams params = new VWO.AdditionalParams();
    params.setCustomVariables(customVariables);
    boolean isFeatureEnabled = vwoInstance.isFeatureEnabled(calledCampaign, "Ashley", params);
    Object variableValue = vwoInstance.getFeatureVariableValue(calledCampaign, "STRING_VARIABLE", "Ashley", params);
    assertNull(variableValue);
    assertFalse(isFeatureEnabled);

    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setSegments(new LinkedHashMap<>());
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setSegments(new LinkedHashMap<>());

    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setPercentTraffic(0);
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setPercentTraffic(0);

    isFeatureEnabled = vwoInstance.isFeatureEnabled(calledCampaign, "Ashley");
    variableValue = vwoInstance.getFeatureVariableValue(calledCampaign, "STRING_VARIABLE", "Ashley");
    assertNull(variableValue);
    assertFalse(isFeatureEnabled);
  }

  @Test
  public void preSegmentationFailedForCalledCampaign() {
    LOGGER.debug("should return false as called campaign does not satisfy the pre-segmentation condition");
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey();
    String otherCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).getKey();

    Map<String, String> customVariables = new HashMap<String, String>() {{
      put("browser", "chrome");
    }};

    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setSegments(getSegmentData("chrome", "false"));
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setSegments(getSegmentData("browser", "chrome"));

    VWOAdditionalParams params = new VWO.AdditionalParams();
    params.setCustomVariables(customVariables);
    boolean isFeatureEnabled = vwoInstance.isFeatureEnabled(calledCampaign, "Ashley", params);
    Object variableValue = vwoInstance.getFeatureVariableValue(calledCampaign, "STRING_VARIABLE", "Ashley", params);
    assertNull(variableValue);
    assertFalse(isFeatureEnabled);

    isFeatureEnabled = vwoInstance.isFeatureEnabled(otherCampaign, "Ashley", params);
    variableValue = vwoInstance.getFeatureVariableValue(otherCampaign, "STRING_VARIABLE", "Ashley", params);
    assertNotNull(variableValue);
    assertTrue(isFeatureEnabled);

    //now changing traffic of called campaign to 0 and other campaign to 100
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setPercentTraffic(0);
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setPercentTraffic(100);
    isFeatureEnabled = vwoInstance.isFeatureEnabled(calledCampaign, "Ashley", params);
    assertFalse(isFeatureEnabled);
    isFeatureEnabled = vwoInstance.isFeatureEnabled(otherCampaign, "Ashley", params);
    assertTrue(isFeatureEnabled);
  }

  @Test
  public void preSegmentationPassedForCalledCampaign() {
    LOGGER.debug("should return true as only called campaign satisfies the pre-segmentation condition");
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey();
    String otherCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).getKey();

    Map<String, String> customVariables = new HashMap<String, String>() {{
      put("browser", "chrome");
    }};

    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setSegments(getSegmentData("chrome", "false"));
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setSegments(getSegmentData("browser", "chrome"));

    VWOAdditionalParams params = new VWO.AdditionalParams();
    params.setCustomVariables(customVariables);
    boolean isFeatureEnabled = vwoInstance.isFeatureEnabled(calledCampaign, "Ashley", params);
    Object variableValue = vwoInstance.getFeatureVariableValue(calledCampaign, "STRING_VARIABLE", "Ashley", params);
    assertNotNull(variableValue);
    assertTrue(isFeatureEnabled);

    //for other campaign
    isFeatureEnabled = vwoInstance.isFeatureEnabled(otherCampaign, "Ashley", params);
    assertFalse(isFeatureEnabled);
  }

  @Test
  public void calledCampaignWinner() {
    LOGGER.debug("should return true/variationName as called campaign is the winner campaign after traffic normalization");
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withCustomLogger(getCustomLogger()).withDevelopmentMode(true).build();
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey();
    String otherCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).getKey();

    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setPercentTraffic(100);
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setPercentTraffic(100);
    boolean variationName = vwoInstance.isFeatureEnabled(calledCampaign, "Ashley");
    Object variableValue = vwoInstance.getFeatureVariableValue(calledCampaign, "STRING_VARIABLE", "Ashley");
    assertTrue(variationName);
    assertNotNull(variableValue);

    variationName = vwoInstance.isFeatureEnabled(otherCampaign, "Ashley");
    variableValue = vwoInstance.getFeatureVariableValue(otherCampaign, "STRING_VARIABLE", "Ashley");
    assertFalse(variationName);
    assertNull(variableValue);
  }

  @Test
  public void calledCampaignNotWinner() {
    LOGGER.debug("should return null as called campaign is the not winner campaign after traffic normalization");
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withDevelopmentMode(true).build();
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).getKey();
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(0).setPercentTraffic(100);
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(1).setPercentTraffic(100);
    boolean variationName = vwoInstance.isFeatureEnabled(calledCampaign, "lisa");
    Object variableValue = vwoInstance.getFeatureVariableValue(calledCampaign, "STRING_VARIABLE", "lisa");
    assertFalse(variationName);
    assertNull(variableValue);
  }

  @Test
  public void equalTrafficDistribution() {
    LOGGER.debug("should return variation after equally distributing traffic among eligible campaigns");
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withDevelopmentMode(true).build();
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).setPercentTraffic(80);
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(3).setPercentTraffic(50);
    String variationName = vwoInstance.activate(calledCampaign, "Ashley");
    assertEquals(variationName, "Variation-1");
  }

  @Test
  public void allNewCampaignsToTheUser() {
    LOGGER.debug("when both the campaigns are new to the user");
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    String otherCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(3).getKey();
    String variationName = vwoInstance.activate(calledCampaign, "Ashley");
    assertEquals(variationName, "Control");
    variationName = vwoInstance.activate(otherCampaign, "Ashley");
    assertNull(variationName);
  }

  @Test
  public void newCampaignAddedToTheGroup() {
    LOGGER.debug("when user was already a part of a campaign and new campaign is added to the group");
    campaignStorageArray.clear();
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withUserStorage(getUserStorage(campaignStorageArray)).withDevelopmentMode(true).build();
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    String otherCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(4).getKey();

    String variationName = vwoInstance.activate(calledCampaign, "Ashley");
    assertEquals(variationName, "Control");

    vwoInstance.getSettingFile().getSettings().getCampaignGroups().put("164", 2);
    vwoInstance.getSettingFile().getSettings().getGroups().get("2").getCampaigns().add(164);
    vwoInstance.getSettingFile().getSettings().getCampaigns().get(4).setPercentTraffic(100);

    variationName = vwoInstance.activate(otherCampaign, "Ashley");
    assertNull(variationName);
  }

  @Test
  public void viewedCampaignRemovedFromTheGroup() {
    LOGGER.debug("when a viewed campaign is removed from the MEG group");
    campaignStorageArray.clear();
    vwoInstance = VWO.launch(Settings.MEG_TRAFFIC_100).withUserStorage(getUserStorage(campaignStorageArray)).withDevelopmentMode(true).build();
    String calledCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(2).getKey();
    String otherCampaign = vwoInstance.getSettingFile().getSettings().getCampaigns().get(4).getKey();

    String variationName = vwoInstance.activate(calledCampaign, "Ashley");
    assertEquals(variationName, "Control");

    vwoInstance.getSettingFile().getSettings().getCampaignGroups().remove("162");
    vwoInstance.getSettingFile().getSettings().getGroups().get("2").getCampaigns().remove(0);

    variationName = vwoInstance.activate(calledCampaign, "Ashley");
    assertEquals(variationName, "Control");
  }



  private Storage.User getUserStorage(ArrayList<Map<String, String>> campaignStorageArray) {
    return new Storage.User() {
      @Override
      public Map<String, String> get(String userId, String campaignName) {
        for (Map<String, String> savedCampaign : campaignStorageArray) {
          if (savedCampaign.get("userId").equals(userId) && savedCampaign.get("campaignKey").equals(campaignName)) {
            return savedCampaign;
          }
        }
        return null;
      }

      @Override
      public void set(Map<String, String> map) {
        campaignStorageArray.add(map);
      }
    };
  }


  private LinkedHashMap getSegmentData(String key, Object value) {
    Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    Map<String, Map<String, Object>> customVariableMap = new HashMap<>();
    customVariableMap.put("custom_variable", data);

    List<Map<String, ?>> or = new ArrayList<>();
    or.add(customVariableMap);


    Map<String, List<Map<String, ?>>> segments = new HashMap<>();
    segments.put("or", or);

    return new LinkedHashMap(segments);
  }

  public static VWOLogger getCustomLogger() {
    return new VWOLogger(VWO.Enums.LOGGER_LEVEL.DEBUG.value()) {

      @Override
      public void trace(String message, Object... params) {
          System.out.println(message);
      }

      @Override
      public void debug(String message, Object... params) {
        System.out.println(message);

      }

      @Override
      public void info(String message, Object... params) {
        System.out.println(message);

      }

      @Override
      public void warn(String message, Object... params) {
        System.out.println(message);

      }

      @Override
      public void error(String message, Object... params) {
        System.out.println(message);
      }
    };
  }
}
