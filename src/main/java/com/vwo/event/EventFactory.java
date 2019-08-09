package com.vwo.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.URIConstants;
import com.vwo.config.ProjectConfig;
import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.logger.LoggerManager;
import com.vwo.models.Campaign;
import com.vwo.models.Goal;
import com.vwo.models.SettingFileConfig;
import com.vwo.models.Variation;
import javafx.util.Pair;

import java.time.Instant;
import java.util.Map;

public class EventFactory {

    public static final String VWO_HOST = URIConstants.BASE_URL.toString();
    public static final String IMPRESSION_PATH = URIConstants.TRACK_USER.toString();
    public static final String GOAL_PATH= URIConstants.TRACK_GOAL.toString();

    private static final ObjectMapper objectMapper= new ObjectMapper();
    private static final LoggerManager LOGGER = LoggerManager.getLogger(EventFactory.class);

    public static DispatchEvent createImpressionLogEvent(ProjectConfig projectConfig, Campaign campaignTestKey, String userId, Variation variation){
        SettingFileConfig settingFileConfig = projectConfig.getSettingFileConfig();
        Event impressionEvent =
                Event.Builder.getInstance()
                        .withaccount_id(settingFileConfig.getAccountId())
                        .withexperiment_id(campaignTestKey.getId())
                        .withRandom(Math.random())
                        .withAp()
                        .withEd()
                        .withuId(userId)
                        .withUuid(settingFileConfig.getAccountId(),userId)
                        .withsId(Instant.now().getEpochSecond())
                        .withVariation(variation.getId()).build();

        LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.TRACK_USER_IMPRESSION_CREATED.value(new Pair<>("userId", userId), new Pair<>("impressionEvent", impressionEvent.toString())));

        Map<String,Object> map = objectMapper.convertValue(impressionEvent, Map.class);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return new DispatchEvent(VWO_HOST, IMPRESSION_PATH, map, DispatchEvent.RequestMethod.GET,null);
    }

    public static DispatchEvent createGoalLogEvent(ProjectConfig projectConfig, Campaign campaignTestKey, String userId, Goal goal, Variation variation){
        SettingFileConfig settingFileConfig = projectConfig.getSettingFileConfig();
        Event goalEvent =
                Event.Builder.getInstance()
                        .withaccount_id(settingFileConfig.getAccountId())
                        .withexperiment_id(campaignTestKey.getId())
                        .withRandom(Math.random())
                        .withuId(userId)
                        .withAp()
                        .withUuid(settingFileConfig.getAccountId(),userId)
                        .withgoal_id(goal.getId())
                        .withsId(Instant.now().getEpochSecond())
                        .withVariation(variation.getId()).build();

        LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.TRACK_GOAL_IMPRESSION_CREATED.value(new Pair<>("goal", goalEvent.toString()), new Pair<>("userId", userId)));

        Map<String,Object> map = objectMapper.convertValue(goalEvent, Map.class);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return new DispatchEvent(VWO_HOST, GOAL_PATH, map, DispatchEvent.RequestMethod.GET,null);
    }
}
