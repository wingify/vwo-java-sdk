package com.vwo.bucketing;

import com.vwo.models.Variation;
import com.vwo.models.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bucketer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bucketer.class);
    private static final int MAX_TRAFFIC_VALUE = 10000;
    private static final int MAX_PERCENT_TRAFFIC = 100;
    private static final int MURMUR_HASH_SEED = 1;

//       USER_HASH_BUCKET_VALUE: '({file}): userId:{userId} having hash:{hashValue} got bucketValue:{bucketValue}',
//    VARIATION_HASH_BUCKET_VALUE:
//            '({file}): userId:{userId} for campaign:{campaignTestKey} having percent traffic:{percentTraffic} got hash-value:{hashValue} and bucket value:{bucketValue}',
//

    public static long isUserInCampaign(Campaign campaign, String userId) {

        String key = userId;
        LOGGER.info("Is userId: {}  part of campaign {} ?",userId,campaign.getKey());
        int murmurHash = Murmur3.hash32(key.getBytes(),0,key.length(),MURMUR_HASH_SEED);
        Long signed_murmurHash = (murmurHash & 0xFFFFFFFFL);
        LOGGER.debug("userId:{} having hash:{}",userId,signed_murmurHash);
        int bucketValueforUser = generateBucketValue(signed_murmurHash,MAX_PERCENT_TRAFFIC,1);
        LOGGER.debug("campaign:{} having traffic allocation:{} assigned value:{} to userId:{}",
                campaign.getKey(),campaign.getPercentTraffic(),bucketValueforUser,userId);
        if (bucketValueforUser > campaign.getPercentTraffic()) {
            LOGGER.debug("UserId:{} for campaign:{} did not become part of campaign",userId,campaign.getKey());
            return -1;
        } else
            return signed_murmurHash;
    }

    private static int generateBucketValue(long murmurHash, int maxTrafficValue,double multiplier) {
        double ratio = (double) murmurHash / Math.pow(2, 32);
        int multipliedValue =(int) (ratio * maxTrafficValue * multiplier);
        return multipliedValue+1;
    }


    public Variation getBucket(Campaign campaign, String userId) {

        if (!campaign.getStatus().equalsIgnoreCase("RUNNING")) {
            LOGGER.error("Campaign is Inactive. Unable to process.");
            return null;
        }
        long murmurHash = isUserInCampaign(campaign, userId);
        if (murmurHash != -1) {
            double multiplier= MAX_TRAFFIC_VALUE / campaign.getPercentTraffic() / 100;
            int bucketValueForVariation=generateBucketValue(murmurHash,MAX_TRAFFIC_VALUE,multiplier);
            LOGGER.debug("userId:{} for campaign:{} having percent traffic:{} got hash-value:{} and bucket value:{}",
                    userId,campaign.getKey(),campaign.getPercentTraffic(),bucketValueForVariation);
            for (Variation variation : campaign.getVariations()) {
                if (bucketValueForVariation >= variation.getStartRangeVariation() &&
                        bucketValueForVariation <= variation.getEndRangeVariation()) {
                    LOGGER.info("userId:{} for campaign:{} got variationName:{}",userId,
                            campaign.getKey(),variation.getName());
                    return variation;
                }
            }

        }
        return null;
    }

}
