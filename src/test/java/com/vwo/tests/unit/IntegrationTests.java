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

package com.vwo.tests.unit;
import com.vwo.VWO;
import com.vwo.VWOAdditionalParams;
import com.vwo.services.integrations.IntegrationEventListener;
import com.vwo.tests.data.Settings;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests {
  private static String userId = TestUtils.getRandomUser();
  private Map<String, Object> integrationsData = new HashMap<String, Object>();

  @Test
  public void integrationABTest() {
    //when the variation is fetched using the murmur logic.
    IntegrationEventListener integrationsSpy = Mockito.spy(this.getIntegrationData());
    VWO vwoInstance = VWO.launch(Settings.AB_TRAFFIC_100_WEIGHT_33_33_33).withIntegrations(getIntegrationData()).build();
    vwoInstance.activate("AB_TRAFFIC_100_WEIGHT_33_33_33", userId);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), null);
    assertEquals(integrationsData.get("isUserWhitelisted"), false);
    assertEquals(integrationsData.get("campaignKey"), "AB_TRAFFIC_100_WEIGHT_33_33_33");

    integrationsData = new HashMap<>();
    //getVariation API
    vwoInstance.getVariationName("AB_TRAFFIC_100_WEIGHT_33_33_33", userId);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), null);
    assertEquals(integrationsData.get("isUserWhitelisted"), false);
    assertEquals(integrationsData.get("campaignKey"), "AB_TRAFFIC_100_WEIGHT_33_33_33");

    //with whitelisting
    vwoInstance = VWO.launch(Settings.USER_WHITELISTING_AB_TEST_TRAFFIC_100).withIntegrations(getIntegrationData()).build();
    VWOAdditionalParams options = new VWOAdditionalParams();
    options.setVariationTargetingVariables(new HashMap<String, Object>() {{
      put("safari", true);
    }});
    vwoInstance.activate("T_100_W_33_33_33_WS_WW", userId, options);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), null);
    assertEquals(integrationsData.get("isUserWhitelisted"), true);
    assertEquals(integrationsData.get("variationName"), "Control");
    assertEquals(integrationsData.get("campaignKey"), "T_100_W_33_33_33_WS_WW");

    integrationsData = new HashMap<>();
    vwoInstance.getVariationName("T_100_W_33_33_33_WS_WW", userId, options);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), null);
    assertEquals(integrationsData.get("isUserWhitelisted"), true);
    assertEquals(integrationsData.get("variationName"), "Control");
    assertEquals(integrationsData.get("campaignKey"), "T_100_W_33_33_33_WS_WW");
  }

  @Test
  public void integrationFeatureRolloutTest() {
    //when the variation is fetched using the murmur logic.
    IntegrationEventListener integrationsSpy = Mockito.spy(this.getIntegrationData());
    integrationsData = new HashMap<>();
    VWO vwoInstance = VWO.launch(Settings.FEATURE_ROLLOUT_TRAFFIC_100).withIntegrations(getIntegrationData()).build();
    vwoInstance.isFeatureEnabled("FR_T_100_W_100", userId);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), true);
    assertEquals(integrationsData.get("isUserWhitelisted"), false);
    assertEquals(integrationsData.get("campaignKey"), "FR_T_100_W_100");

    integrationsData = new HashMap<>();

    //for getFeatureVariableValueApi
    vwoInstance = VWO.launch(Settings.FEATURE_ROLLOUT_TRAFFIC_100).withIntegrations(getIntegrationData()).build();
    vwoInstance.getFeatureVariableValue("FR_T_100_W_100", "STRING_VARIABLE", userId);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), true);
    assertEquals(integrationsData.get("isUserWhitelisted"), false);
    assertEquals(integrationsData.get("variationName"), null);
    assertEquals(integrationsData.get("variationId"), null);
  }

  @Test
  public void integrationFeatureTestCampaign() {
    //when the variation is fetched using the murmur logic.
    IntegrationEventListener integrationsSpy = Mockito.spy(this.getIntegrationData());
    integrationsData = new HashMap<>();
    VWO vwoInstance = VWO.launch(Settings.FEATURE_TEST_TRAFFIC_100).withIntegrations(getIntegrationData()).build();
    vwoInstance.isFeatureEnabled("FT_T_100_W_10_20_30_40", "Ashley");
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), true);
    assertEquals(integrationsData.get("isUserWhitelisted"), false);
    assertEquals(integrationsData.get("campaignKey"), "FT_T_100_W_10_20_30_40");

    integrationsData = new HashMap<>();

    //for getFeatureVariableValueApi
    vwoInstance = VWO.launch(Settings.USER_WHITELISTING_FEATURE_TEST_TRAFFIC_100).withIntegrations(getIntegrationData()).build();
    VWOAdditionalParams options = new VWOAdditionalParams();
    options.setVariationTargetingVariables(new HashMap<String, Object>() {{
      put("chrome", false);
    }});
    vwoInstance.getFeatureVariableValue("FT_100_W_33_33_33_WS_WW", "STRING_VARIABLE", "Ashley", options);
    assertEquals(integrationsData.get("fromUserStorageService"), false);
    assertEquals(integrationsData.get("isFeatureEnabled"), false);
    assertEquals(integrationsData.get("isUserWhitelisted"), true);
    assertEquals(integrationsData.get("variationName"), "Variation-2");
    assertEquals(integrationsData.get("variationId"), 3);
  }



  public IntegrationEventListener getIntegrationData() {
    return new IntegrationEventListener() {
      @Override
      public void onEvent(Map<String, Object> properties) {
        integrationsData = properties;
      }
    };
  }
}
