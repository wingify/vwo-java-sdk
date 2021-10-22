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

package com.vwo.services.core;

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.models.response.Campaign;
import com.vwo.models.response.Variation;
import com.vwo.utils.CampaignUtils;

import java.util.HashMap;
import java.util.List;

public class BucketingService {

  private static final Logger LOGGER = Logger.getLogger(BucketingService.class);

  public static final int MAX_TRAFFIC_VALUE = 10000;
  private static final int MAX_PERCENT_TRAFFIC = 100;
  private static final int SEED_VALUE = 1;

  /**
   * Get the hash value for used ID.
   *
   * @param userId - User ID
   * @param traffic - Campaign traffic
   * @return signed murmur hash value
   */

  public static long getUserHashForCampaign(String seed, String userId, int traffic, boolean disableLogs) {

    int murmurHash = Murmur3.hash32(seed.getBytes(), 0, seed.length(), SEED_VALUE);

    /**
     * Took reference from StackOverflow (https://stackoverflow.com/) to:
     * Convert the int to unsigned long value
     * Author - Mysticial (https://stackoverflow.com/users/922184/mysticial)
     * Source - https://stackoverflow.com/questions/9578639/best-way-to-convert-a-signed-integer-to-an-unsigned-long
     */
    Long signedMurmurHash = (murmurHash & 0xFFFFFFFFL);
    int bucketValueOfUser = BucketingService.getMultipliedHashValue(signedMurmurHash, MAX_PERCENT_TRAFFIC, 1);

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.USER_HASH_BUCKET_VALUE.value(new HashMap<String, String>() {
      {
        put("bucketValue", String.valueOf(bucketValueOfUser));
        put("userId", userId);
        put("hashValue", String.valueOf(murmurHash));
      }
    }), disableLogs);

    return bucketValueOfUser > traffic ? -1 : signedMurmurHash;
  }

  public Object getUserVariation(Object variations, Campaign campaign, int campaignTraffic, String userId) {
    long murmurHash = BucketingService.getUserHashForCampaign(CampaignUtils.getBucketingSeed(userId, campaign, null), userId, campaignTraffic, false);


    if (murmurHash != -1) {
      double multiplier = ((double) MAX_TRAFFIC_VALUE) / campaignTraffic / 100;
      int variationHashValue = BucketingService.getMultipliedHashValue(murmurHash, MAX_TRAFFIC_VALUE, multiplier);

      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.VARIATION_HASH_VALUE.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("variationHashValue", String.valueOf(variationHashValue));
          put("userId", userId);
          put("traffic", String.valueOf(campaignTraffic));
          put("hashValue", String.valueOf(murmurHash));
        }
      }));

      return getAllocatedItem(variations, variationHashValue);
    }

    LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.USER_NOT_PART_OF_CAMPAIGN.value(new HashMap<String, String>() {
      {
        put("campaignKey", campaign.getKey().toString());
        put("userId", userId);
      }
    }));
    return null;
  }

  /**
   * Get the updated hash value.
   *
   * @param murmurHash Murmur hash
   * @param maxTrafficValue Traffic value
   * @param multiplier Multiplier constant
   * @return multiplied hash value
   */
  public static int getMultipliedHashValue(long murmurHash, int maxTrafficValue, double multiplier) {
    double ratio = (double) murmurHash / Math.pow(2, 32);
    int multipliedValue = (int) (ratio * maxTrafficValue * multiplier);
    return multipliedValue + 1;
  }

  /**
   * Get the variation according to traffic and user hash value.
   *
   * @param itemList Campaign/Variations list
   * @param hashValue hash value
   * @return variation object
   */
  public Object getAllocatedItem(Object itemList, int hashValue) {
    for (Object item: (List<Object>) itemList) {
      if (item instanceof Variation) {
        if (hashValue >= ((Variation) item).getStartRangeVariation() && hashValue <= ((Variation) item).getEndRangeVariation()) {
          return item;
        }
      } else {
        if (hashValue >= ((Campaign) item).getStartRange() && hashValue <= ((Campaign) item).getEndRange()) {
          return item;
        }
      }

    }
    return null;
  }
}
