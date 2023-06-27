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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.VWOAdditionalParams;
import com.vwo.models.response.BatchEventData;
import com.vwo.models.response.Settings;
import com.vwo.logger.Logger;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.utils.TestUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class BucketingNewTests {
  @Test
  public void testWithoutSeedWithoutIsOB() {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.SettingsNewBucketingAlgo.SETTINGS_WITHOUT_SEED_WITHOUT_ISOB).build();
    String campaignKey = "BUCKET_ALGO_WITHOUT_SEED";
    String[] users = new String[] {"Ashley", "Bill", "Chris", "Dominic", "Emma",
    		"Faizan", "Gimmy", "Harry", "Ian", "John",
    		"King", "Lisa", "Mona", "Nina", "Olivia",
    		"Pete", "Queen", "Robert", "Sarah", "Tierra",
    		"Una", "Varun", "Will", "Xin", "You", "Zeba"};
    String[] variations = new String[] {"Control", "Control", "Variation-1", "Variation-1", "Control",
    		"Control", "Variation-1", "Control", "Control", "Control",
    		"Variation-1", "Control", "Control", "Variation-1", "Control",
    		"Variation-1", "Variation-1", "Control", "Control", "Variation-1",
    		"Control", "Control", "Variation-1", "Control", "Variation-1", "Control"};
    String variation;

    // parse through the users and match the variations
    for(int x=0; x<users.length; x++) {
    	variation = vwoInstance.activate(campaignKey, users[x]);

    	// verify corresponding variation
    	assertEquals(variation, variations[x]);
    }
  }

  @Test
  public void testWithSeedWithoutIsOB() {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.SettingsNewBucketingAlgo.SETTINGS_WITH_SEED_WITHOUT_ISOB).build();
    String campaignKey = "BUCKET_ALGO_WITH_SEED";
    String[] users = new String[] {"Ashley", "Bill", "Chris", "Dominic", "Emma",
    		"Faizan", "Gimmy", "Harry", "Ian", "John",
    		"King", "Lisa", "Mona", "Nina", "Olivia",
    		"Pete", "Queen", "Robert", "Sarah", "Tierra",
    		"Una", "Varun", "Will", "Xin", "You", "Zeba"};
    String[] variations = new String[] {"Control", "Control", "Variation-1", "Variation-1", "Control",
    		"Control", "Variation-1", "Control", "Control", "Control",
    		"Variation-1", "Control", "Control", "Variation-1", "Control",
    		"Variation-1", "Variation-1", "Control", "Control", "Variation-1",
    		"Control", "Control", "Variation-1", "Control", "Variation-1", "Control"};
    String variation;

    // parse through the users and match the variations
    for(int x=0; x<users.length; x++) {
    	variation = vwoInstance.activate(campaignKey, users[x]);

    	// verify corresponding variation
    	assertEquals(variation, variations[x]);
    }
  }

  @Test
  public void testWithIsNBWithIsOB() {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.SettingsNewBucketingAlgo.SETTINGS_WITH_ISNB_WITH_ISOB).build();
    String campaignKey = "BUCKET_ALGO_WITH_SEED_WITH_isNB_WITH_isOB";
    String[] users = new String[] {"Ashley", "Bill", "Chris", "Dominic", "Emma",
    		"Faizan", "Gimmy", "Harry", "Ian", "John",
    		"King", "Lisa", "Mona", "Nina", "Olivia",
    		"Pete", "Queen", "Robert", "Sarah", "Tierra",
    		"Una", "Varun", "Will", "Xin", "You", "Zeba"};
    String[] variations = new String[] {"Variation-1", "Variation-1", "Variation-1", "Variation-1", "Variation-1",
    		"Control", "Variation-1", "Variation-1", "Variation-1", "Variation-1",
    		"Variation-1", "Control", "Variation-1", "Control", "Variation-1",
    		"Variation-1", "Control", "Control", "Control", "Control",
    		"Control", "Variation-1", "Variation-1", "Variation-1", "Variation-1", "Control"};
    String variation;

    // parse through the users and match the variations
    for(int x=0; x<users.length; x++) {
    	variation = vwoInstance.activate(campaignKey, users[x]);

    	// verify corresponding variation
    	assertEquals(variation, variations[x]);
    }
  }

  @Test
  public void testWithIsNBWithoutIsOB() {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.SettingsNewBucketingAlgo.SETTINGS_WITH_ISNB_WITHOUT_ISOB).build();
    String campaignKey = "BUCKET_ALGO_WITH_SEED_WITH_isNB_WITHOUT_isOB";
    String[] users = new String[] {"Ashley", "Bill", "Chris", "Dominic", "Emma",
    		"Faizan", "Gimmy", "Harry", "Ian", "John",
    		"King", "Lisa", "Mona", "Nina", "Olivia",
    		"Pete", "Queen", "Robert", "Sarah", "Tierra",
    		"Una", "Varun", "Will", "Xin", "You", "Zeba"};
    String[] variations = new String[] {"Control", "Control", "Variation-1", "Variation-1", "Control",
    		"Control", "Variation-1", "Control", "Control", "Control",
    		"Variation-1", "Control", "Control", "Variation-1", "Control",
    		"Variation-1", "Variation-1", "Control", "Control", "Variation-1",
    		"Control", "Control", "Variation-1", "Control", "Variation-1", "Control"};
    String variation;

    // parse through the users and match the variations
    for(int x=0; x<users.length; x++) {
    	variation = vwoInstance.activate(campaignKey, users[x]);

    	// verify corresponding variation
    	assertEquals(variation, variations[x]);
    }
  }

  @Test
  public void testWithoutSeedWithIsNBWithoutIsOB() {
    VWO vwoInstance = VWO.launch(com.vwo.tests.data.SettingsNewBucketingAlgo.SETTINGS_WITHOUT_SEED_WITH_ISNB_WITHOUT_ISOB).build();
    String campaignKey = "BUCKET_ALGO_WITHOUT_SEED_FLAG_WITH_isNB_WITHOUT_isOB";
    String[] users = new String[] {"Ashley", "Bill", "Chris", "Dominic", "Emma",
    		"Faizan", "Gimmy", "Harry", "Ian", "John",
    		"King", "Lisa", "Mona", "Nina", "Olivia",
    		"Pete", "Queen", "Robert", "Sarah", "Tierra",
    		"Una", "Varun", "Will", "Xin", "You", "Zeba"};
    String[] variations = new String[] {"Control", "Control", "Variation-1", "Variation-1", "Control",
    		"Control", "Variation-1", "Control", "Control", "Control",
    		"Variation-1", "Control", "Control", "Variation-1", "Control",
    		"Variation-1", "Variation-1", "Control", "Control", "Variation-1",
    		"Control", "Control", "Variation-1", "Control", "Variation-1", "Control"};
    String variation;

    // parse through the users and match the variations
    for(int x=0; x<users.length; x++) {
    	variation = vwoInstance.activate(campaignKey, users[x]);

    	// verify corresponding variation
    	assertEquals(variation, variations[x]);
    }
  }
}
