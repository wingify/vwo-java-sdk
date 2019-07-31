package com.vwo.userprofile;

import java.util.Map;

public interface UserProfileService {

    String userId = "user_id";
    String campaignKey = "campaign_key";
    String variationKey = "variation_key";

    Map<String,Object> lookup(String userId,String campaignTestKey) throws Exception;

    void save(Map<String,Object> userProfile) throws Exception;
}
