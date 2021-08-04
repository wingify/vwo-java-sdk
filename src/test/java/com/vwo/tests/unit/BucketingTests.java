package com.vwo.tests.unit;

import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
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
