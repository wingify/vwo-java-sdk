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

package com.vwo.services.core;

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Variable;
import com.vwo.models.Variation;

import java.util.HashMap;

public class BucketingService {

  private static final Logger LOGGER = Logger.getLogger(BucketingService.class);

  private static final int MAX_TRAFFIC_VALUE = 10000;
  private static final int MAX_PERCENT_TRAFFIC = 100;
  private static final int SEED_VALUE = 1;

  /**
   * Get the hash value for used ID.
   *
   * @param campaign - Campaign object
   * @param userId - User ID
   * @return signed murmur hash value
   */
  public static long getUserHashForCampaign(Campaign campaign, String userId) {
    int murmurHash = Murmur3.hash32(userId.getBytes(), 0, userId.length(), SEED_VALUE);

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
    }));

    if (bucketValueOfUser > campaign.getPercentTraffic()) {
      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.USER_NOT_PART_OF_CAMPAIGN.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("userId", userId);
        }
      }));
      return -1;
    } else {
      return signedMurmurHash;
    }
  }

  /**
   * Get the updated hash value.
   *
   * @param murmurHash Murmur hash
   * @param maxTrafficValue Traffic value
   * @param multiplier Multiplier constant
   * @return multiplied hash value
   */
  private static int getMultipliedHashValue(long murmurHash, int maxTrafficValue, double multiplier) {
    double ratio = (double) murmurHash / Math.pow(2, 32);
    int multipliedValue = (int) (ratio * maxTrafficValue * multiplier);
    return multipliedValue + 1;
  }

  public Variation getUserVariation(Campaign campaign, String userId) {
    long murmurHash = BucketingService.getUserHashForCampaign(campaign, userId);

    if (murmurHash != -1) {
      double multiplier = ((double) MAX_TRAFFIC_VALUE) / campaign.getPercentTraffic() / 100;
      int variationHashValue = BucketingService.getMultipliedHashValue(murmurHash, MAX_TRAFFIC_VALUE, multiplier);

      LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.VARIATION_HASH_VALUE.value(new HashMap<String, String>() {
        {
          put("campaignKey", campaign.getKey());
          put("variationHashValue", String.valueOf(variationHashValue));
          put("userId", userId);
          put("traffic", String.valueOf(campaign.getPercentTraffic()));
          put("hashValue", String.valueOf(murmurHash));
        }
      }));

      return getVariation(campaign, variationHashValue);
    }
    return null;
  }

  /**
   * Get the variation according to traffic and user hash value.
   *
   * @param campaign campaign setting
   * @param variationHashValue variation hash value
   * @return variation object
   */
  private Variation getVariation(Campaign campaign, int variationHashValue) {
    for (Variation variation: campaign.getVariations()) {
      if (variationHashValue >= variation.getStartRangeVariation() && variationHashValue <= variation.getEndRangeVariation()) {
        return variation;
      }
    }
    return null;
  }
}
