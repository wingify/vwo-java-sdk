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

import com.vwo.logger.Logger;
import com.vwo.models.response.Campaign;
import com.vwo.services.core.BucketingService;
import com.vwo.tests.data.UserExpectations;
import com.vwo.tests.e2e.MEGTests;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class BucketingTests {
  private static final Logger LOGGER = Logger.getLogger(MEGTests.class);

  @Test
  public void getBucketValueForUser64Test() {
    Campaign campaign = new Campaign();
    campaign.setId(1);
    campaign.setBucketingSeedEnabled(true);
    Long bucketHash = BucketingService.getUserHashForCampaign(campaign.isBucketingSeedEnabled() ? (campaign.getId() + "_" + "someone@mail.com") : "someone@mail.com", "someone@mail.com", 100, true);
    int variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
    assertEquals(variationHashValue, 2444);

    campaign.setBucketingSeedEnabled(false);

     bucketHash = BucketingService.getUserHashForCampaign(campaign.isBucketingSeedEnabled() ? (campaign.getId() + "_" + "someone@mail.com") : "someone@mail.com", "someone@mail.com", 100, true);
     variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
    assertEquals(variationHashValue, 6361);
  }

  @Test
  public void getBucketValueForUser50Test() {
    Campaign campaign = new Campaign();
    campaign.setId(1);
    campaign.setBucketingSeedEnabled(true);
    Long bucketHash = BucketingService.getUserHashForCampaign(campaign.isBucketingSeedEnabled() ? (campaign.getId() + "_" + "1111111111111111") : "1111111111111111", "someone@mail.com", 100, true);
    int variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
    assertEquals(variationHashValue, 8177);

    campaign.setBucketingSeedEnabled(false);

     bucketHash = BucketingService.getUserHashForCampaign(campaign.isBucketingSeedEnabled() ? (campaign.getId() + "_" + "1111111111111111") : "1111111111111111", "someone@mail.com", 100, true);
     variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
    assertEquals(variationHashValue, 4987);
  }

  @Test
  public void getBucketValueForMultipleUserIdsTest() {
    ArrayList<UserExpectations.Bucketing> bucketingData = UserExpectations.bucketingData;

    for (UserExpectations.Bucketing data:  bucketingData) {
      Long bucketHash = BucketingService.getUserHashForCampaign(data.getCampaign().isBucketingSeedEnabled() ? (data.getCampaign().getId() + "_" + data.getUser()) : data.getUser(), data.getUser(), 100, true);
      int variationHashValue = BucketingService.getMultipliedHashValue(bucketHash, BucketingService.MAX_TRAFFIC_VALUE, 1);
      assertEquals(variationHashValue, data.getBucketValue());
    }
  }
}
