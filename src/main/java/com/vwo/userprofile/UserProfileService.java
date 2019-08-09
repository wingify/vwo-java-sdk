package com.vwo.userprofile;

import java.util.Map;

public interface UserProfileService {

    String userId = "userId";
    String campaignKey = "campaign";
    String variationKey = "variationName";

    Map<String, String> lookup(String userId, String campaignTestKey) throws Exception;

    void save(Map<String, String> userProfile) throws Exception;
}
