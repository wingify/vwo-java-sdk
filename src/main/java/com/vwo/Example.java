package com.vwo;


import com.vwo.event.EventDispatcher;
import com.vwo.event.EventHandler;
import com.vwo.config.FileSettingUtils;
import com.vwo.logger.VWOLogger;
import com.vwo.userprofile.UserProfileService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Example {

    private final VWO  vwo;

    public Example(VWO vwo) {
        this.vwo = vwo;
    }

    public static void main(String[] args) {

        String settings = VWO.getSetting("","");

        System.out.println(settings);

        UserProfileService userProfileService= new UserProfileService() {
            @Override
            public Map<String, String> lookup(String userId, String campaignKey) throws Exception {
                String campaignId = "FIRST";
                String variationId = "Control";
                Map<String, String> campaignBucketMap = new HashMap<>();

                campaignBucketMap.put(UserProfileService.userId, "Priya");
                campaignBucketMap.put(UserProfileService.campaignKey, campaignId);
                campaignBucketMap.put(UserProfileService.variationKey, variationId);

                return campaignBucketMap;
            }

            @Override
            public void save(Map<String, String> map) throws Exception {

            }
        };

        VWOLogger logger = new VWOLogger() {
            @Override
            public void trace(String var1, Object... var2) {
                System.out.println("Custom Logger [Trace]: " + var1);
            }

            @Override
            public void debug(String var1, Object... var2) {
                System.out.println("Custom Logger [Debug]: " + var1);

            }

            @Override
            public void info(String var1, Object... var2) {
                System.out.println("Custom Logger [Info]: " + var1);

            }

            @Override
            public void warn(String var1, Object... var2) {
                System.out.println("Custom Logger [Warn]: " + var1);

            }

            @Override
            public void error(String var1, Object... var2) {
                System.out.println("Custom Logger [Error]: " + var1 + Arrays.toString(var2));
            }
        };

        VWO vwo_instance = VWO.createInstance(settings).build();

        vwo_instance.track("FIRST","PRIYA","CUSTOM");

        VWO vwo = VWO.createInstance(settings).withUserProfileService(userProfileService).withCustomLogger(logger).build();

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
        linkedList.add("Yatin");
        linkedList.add("Zeba");


        for(String name : linkedList) {
            String variation = vwo.getVariation("DEV_TEST_1", name);
            if(variation != null) {
                System.out.println("User: '" + name + "' of campaign 'DEV_TEST_1' is part - true, got variation: " + variation);
            } else {
                System.out.println("User: '" + name + "' of campaign 'DEV_TEST_1' is part - false, got variation: null");
            }
        }
    }
}
