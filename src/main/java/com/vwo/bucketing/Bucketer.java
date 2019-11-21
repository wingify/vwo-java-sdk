/*
Copyright 2019 Wingify Software Pvt. Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.vwo.bucketing;

import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.logger.LoggerManager;
import com.vwo.models.Campaign;
import com.vwo.models.Variation;

import java.util.HashMap;

public class Bucketer {

  private static final LoggerManager LOGGER = LoggerManager.getLogger(Bucketer.class);

  private static final int MAX_TRAFFIC_VALUE = 10000;
  private static final int MAX_PERCENT_TRAFFIC = 100;
  private static final int MURMUR_HASH_SEED = 1;

  public static long getUserHashForCampaign(Campaign campaign, String userId) {
    int murmurHash = Murmur3.hash32(userId.getBytes(), 0, userId.length(), MURMUR_HASH_SEED);
    /*
      Took reference from StackOverflow (https://stackoverflow.com/) to:
      Convert the int to unsigned long value
      Author - Mysticial (https://stackoverflow.com/users/922184/mysticial)
      Source - https://stackoverflow.com/questions/9578639/best-way-to-convert-a-signed-integer-to-an-unsigned-long
     */
    Long unSignedMurmurHash = (murmurHash & 0xFFFFFFFFL);
    int bucketValueOfUser = Bucketer.getBucketValueForUser(unSignedMurmurHash, MAX_PERCENT_TRAFFIC, 1);

    LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.USER_HASH_BUCKET_VALUE.value(new HashMap<String, String>() {
      {
        put("bucketValue", String.valueOf(bucketValueOfUser));
        put("userId", userId);
        put("hashValue", String.valueOf(murmurHash));
      }
    }));

    if (bucketValueOfUser > campaign.getPercentTraffic()) {
      LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.USER_NOT_PART_OF_CAMPAIGN.value(new HashMap<String, String>() {
        {
          put("campaignTestKey", campaign.getKey());
          put("userId", userId);
        }
      }));
      return -1;
    } else {
      return unSignedMurmurHash;
    }
  }

  private static int getBucketValueForUser(long murmurHash, int maxTrafficValue, double multiplier) {
    double ratio = (double) murmurHash / Math.pow(2, 32);
    int multipliedValue = (int) (ratio * maxTrafficValue * multiplier);
    return multipliedValue + 1;
  }

  public Variation bucketUserToVariation(Campaign campaign, String userId) {
    if (!campaign.getStatus().equalsIgnoreCase("RUNNING")) {
      LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CAMPAIGN_NOT_RUNNING.value());
      return null;
    }

    long murmurHash = Bucketer.getUserHashForCampaign(campaign, userId);

    if (murmurHash != -1) {
      double multiplier = ((double) MAX_TRAFFIC_VALUE) / campaign.getPercentTraffic() / 100;
      int bucketValueForVariation = Bucketer.getBucketValueForUser(murmurHash, MAX_TRAFFIC_VALUE, multiplier);

      LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.VARIATION_HASH_BUCKET_VALUE.value(new HashMap<String, String>() {
        {
          put("campaignTestKey", campaign.getKey());
          put("bucketValue", String.valueOf(bucketValueForVariation));
          put("userId", userId);
          put("traffic", String.valueOf(campaign.getPercentTraffic()));
          put("hashValue", String.valueOf(murmurHash));
        }
      }));

      for (Variation variation : campaign.getVariations()) {
        if (bucketValueForVariation >= variation.getStartRangeVariation() && bucketValueForVariation <= variation.getEndRangeVariation()) {
          return variation;
        }
      }

    }
    return null;
  }

}
