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

package com.vwo.tests.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.logger.Logger;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Settings;
import com.vwo.services.core.BucketingService;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.e2e.MEGTests;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class ValidationTests {
  private static final Logger LOGGER = Logger.getLogger(ValidationTests.class);
  
  @Test
  public void validateExtraParamInCampaign() throws IOException {
    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.
        VALIDATE_SETTINGS_WITH_EXTRA_PARAMS_CAMPAIGN, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.VALIDATE_SETTINGS_WITH_EXTRA_PARAMS_CAMPAIGN).build();
    String variation = vwoInstance.activate(campaignKey, "Ashley");
    
    assertNotNull(vwoInstance);
    assertEquals(variation, "Control");
  }
  
  @Test
  public void validateExtraParamInRoot() throws IOException {
    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.
        VALIDATE_SETTINGS_WITH_EXTRA_PARAMS_ROOT, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.VALIDATE_SETTINGS_WITH_EXTRA_PARAMS_ROOT).build();
    String variation = vwoInstance.activate(campaignKey, "Ashley");
    
    assertNotNull(vwoInstance);
    assertEquals(variation, "Control");
  }
  
  /* @Test
  public void validateExtraParamInCampaignGroup() throws IOException {
    Settings settingsFile = new ObjectMapper().readValue(com.vwo.tests.data.Settings.
        VALIDATE_SETTINGS_WITH_EXTRA_PARAMS_CAMPAIGNGROUP, Settings.class);
    String campaignKey = settingsFile.getCampaigns().get(0).getKey();
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.VALIDATE_SETTINGS_WITH_EXTRA_PARAMS_CAMPAIGN).build();
    String variation = vwoInstance.activate(campaignKey, "Pete");
    
    assertNotNull(vwoInstance);
    assertEquals(variation, "Control");
  }
  */
}
