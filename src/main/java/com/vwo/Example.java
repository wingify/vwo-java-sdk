package com.vwo;


import com.vwo.event.EventDispatcher;
import com.vwo.event.EventHandler;
import com.vwo.config.FileSettingUtils;
import com.vwo.userprofile.UserProfileService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

    public class Example {

        private final VWO  vwo;

        public Example(VWO vwo) {
            this.vwo = vwo;
        }


        public static void main(String[] args) {

           String settingsFle= FileSettingUtils.getSetting("","");

           System.out.println(settingsFle);

            UserProfileService userProfileService= new UserProfileService() {
                @Override
                public Map<String, Object> lookup(String s, String s1) throws Exception {
                    String campaignId = "FIRST";
                    String variationId = "Control";

                    Map<String,Object> campaignKeyMap = new HashMap<>();
                    Map<String, String> variationKeyMap = new HashMap<>();
                    variationKeyMap.put(UserProfileService.variationKey, variationId);
                    campaignKeyMap.put(campaignId,variationKeyMap);

                    //set
                    Map<String, Object> campaignStaticBucketMap = new HashMap<>();
                    campaignStaticBucketMap.put(UserProfileService.userId, "Priya");
                    campaignStaticBucketMap.put(UserProfileService.campaignKey, campaignKeyMap);

                    return campaignStaticBucketMap;
                }

                @Override
                public void save(Map<String, Object> map) throws Exception {

                }
            };

            VWO vwo_instance = VWO.createInstance(settingsFle).build();

            vwo_instance.track("FIRST","PRIYA","CUSTOM");

            EventHandler eventHandler = EventDispatcher.builder().build();
        VWO vwo = VWO.createInstance(settingsFle).withUserProfileService(userProfileService)
                .withEventHandler(eventHandler)
                .build();

        EventHandler eventHandler2 = EventDispatcher.builder().build();

        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("Ashley");
        linkedList.add("Bill");
        linkedList.add("Chris");
        linkedList.add("Dominic");
        linkedList.add("Emma");
        linkedList.add("Faizan");
        linkedList.add("Gimmy");
        linkedList.add("Harry");
        linkedList.add("Ian");
        linkedList.add("John");
        linkedList.add("King");
        linkedList.add("Lisa");
        linkedList.add("Mona");
        linkedList.add("Nina");
        linkedList.add("Olivia");
        linkedList.add("Pete");
        linkedList.add("Queen");
        linkedList.add("Robert");
        linkedList.add("Sarah");
        linkedList.add("Tierra");
        linkedList.add("Una");
        linkedList.add("Varun");
        linkedList.add("Will");
        linkedList.add("Xin");
        linkedList.add("You");
        linkedList.add("Zeba");


        for(String name : linkedList){
           String variation= vwo.getVariation("DEV_TEST_1",name);
             if(variation!=null){
            System.out.println("User:"+name+" of campaign DEV_TEST_1 is part - true, got variation:"+variation);
        }else{
                 System.out.println("User:"+name+" of campaign DEV_TEST_1 is part - false, got variation:null");
             }
        }

    }
}
