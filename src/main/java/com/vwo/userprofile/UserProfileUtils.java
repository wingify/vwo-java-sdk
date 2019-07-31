package com.vwo.userprofile;

import java.util.Map;

public class UserProfileUtils {

    public static boolean isValidUserProfileMap(Map<String,Object> map){
        if(!map.containsKey(UserProfileService.userId)) {
            return false;
        }
        if(!map.containsKey(UserProfileService.campaignKey)){
            return false;
        }
        if(!(map.get(UserProfileService.campaignKey) instanceof Map)){
            return false;
        }
        Map<String, Map<String , String>> campaignMap;
        try{
             campaignMap= ( Map<String, Map<String , String>>) map.get(UserProfileService.campaignKey);
        }catch(ClassCastException classCastException){
           return false;
        }

        for(Map<String,String> variationMap : campaignMap.values() ){
            if(!variationMap.containsKey(UserProfileService.variationKey)){
                return false;
            }
        }
        return true;
    }

}
