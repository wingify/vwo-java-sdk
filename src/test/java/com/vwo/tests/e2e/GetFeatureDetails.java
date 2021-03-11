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

//package com.vwo.tests;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vwo.VWO;
//import com.vwo.logger.Logger;
//import com.vwo.models.Settings;
//import com.vwo.models.Variable;
//import com.vwo.models.Variation;
//import com.vwo.tests.data.Settings;
//import com.vwo.tests.data.UserExpectations;
//import com.vwo.tests.utils.TestUtils;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//public class GetFeatureDetails {
//  private static VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_50_WEIGHT_50_50).build();
//  private static final Logger LOGGER = Logger.getLogger(GetFeatureDetails.class);
//
//  @Test
//  public void validationTests() throws IOException {
//    LOGGER.info("Should return false if no campaignKey is passed");
//    assertNull(vwoInstance.getFeatureDetails(""));
//    assertNull(vwoInstance.getFeatureDetails(null));
//  }
//
//  @Test
//  public void invalidCampaignKeyTest() throws IOException {
//    LOGGER.info("Should test against an invalid campaign key");
//
//    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).build();
//
//    List<Variable> variablesDetails = vwoInstance.getFeatureDetails("INCORRECT_KEY");
//    assertNull(variablesDetails);
//  }
//
//  @Test
//  public void abCampaignTests() throws IOException {
//    LOGGER.info("Should test against an AB campaign settings");
//
//    vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).build();
//    Settings settings = new ObjectMapper().readValue(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33, Settings.class);
//    String campaignKey = settings.getCampaigns().get(0).getKey();
//
//    List<Variable> variablesDetails = vwoInstance.getFeatureDetails(campaignKey);
//    assertNull(variablesDetails);
//  }
//
//  @Test
//  public void featureRolloutTests() throws IOException {
//    LOGGER.info("Should test against a feature rollout campaign settings");
//
//    vwoInstance = VWO.launch(Settings.FEATURE_ROLLOUT_TRAFFIC_100).build();
//    Settings settings = new ObjectMapper().readValue(Settings.FEATURE_ROLLOUT_TRAFFIC_100, Settings.class);
//    String campaignKey = settings.getCampaigns().get(0).getKey();
//
//    List<Variable> variablesDetails = vwoInstance.getFeatureDetails(campaignKey);
//
//    for (int i = 0; i < variablesDetails.size(); i++) {
//      Variable variable = variablesDetails.get(i);
//      Variable expectedVariable = settings.getCampaigns().get(0).getVariables().get(i);
//
//      assertTrue(variable.equals(expectedVariable));
//    }
//  }
//
//  @Test
//  public void featureTestTests() throws IOException {
//    LOGGER.info("Should test against a feature test campaign settings");
//
//    vwoInstance = VWO.launch(Settings.FEATURE_TEST_TRAFFIC_100).build();
//    Settings settings = new ObjectMapper().readValue(Settings.FEATURE_TEST_TRAFFIC_100, Settings.class);
//    String campaignKey = settings.getCampaigns().get(0).getKey();
//
//    List<Variable> variablesDetails = vwoInstance.getFeatureDetails(campaignKey);
//
//    for (int i = 0; i < variablesDetails.size(); i++) {
//      Variable variable = variablesDetails.get(i);
//      Variable expectedVariable = settings.getCampaigns().get(0).getVariations().get(0).getVariables().get(i);
//
//      assertTrue(variable.equals(expectedVariable));
//    }
//  }
//}
