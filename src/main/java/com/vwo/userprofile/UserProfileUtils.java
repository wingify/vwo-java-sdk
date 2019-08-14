package com.vwo.userprofile;

import java.util.Map;

public class UserProfileUtils {

  public static boolean isValidUserProfileMap(Map<String, String> map) {
    return map.containsKey(UserProfileService.userId)
            && map.containsKey(UserProfileService.campaignKey)
            && map.containsKey(UserProfileService.variationKey);
  }

}