package com.vwo.enums;

import javafx.util.Pair;

public class LoggerMessagesEnum {

  public static String getComputedMsg(String msg, Pair<String, String>... pairs) {
    for (Pair<String, String> pair : pairs) {
      String key = pair.getKey();
      String value = pair.getValue();
      msg = msg.replaceAll("\\{\\{" + key + "}}", value); // USe format %s
    }
    return msg;
  }

  public enum DEBUG_MESSAGES {
    FETCHING_ACCOUNT_SETTINGS("Fetching  account settings for ID: {{accountID}}."),
    ACTIVATING_CAMPAIGN("Activating user '{{userId}}'  for variation '{{variation}}'."),
    GOT_STORED_VARIATION("Got stored variation '{{variationName}}' of campaign '{{campaignTestKey}}' for userId '{{userId}}' from UserProfileService."),
    NO_STORED_VARIATION("No stored variation for UserId '{{userId}}' for Campaign '{{campaignTestKey}}' found in UserProfileService."),
    NO_USER_PROFILE_DEFINED("UserProfileService is not defined. Skipping it!"),
    EVALUATED_VARIATION_BUCKET("Evaluated bucket value {{bucketValue}} of user {{userId}} for campaign {{campaignTestKey}} having traffic allocation {{traffic}}."),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignTestKey}}'."),
    SAVED_IN_USER_PROFILE_SERVICE("Successfully saved variation '{{variation}}' of user {{userId}} in UserProfileService."),
    TRACK_USER_IMPRESSION_CREATED("Impression event built for track-user '{{userId}}' - '{{impressionEvent}}'."),
    TRACK_GOAL_IMPRESSION_CREATED("Impression event built for track-goal of user '{{userId}}' -  '{{goalEvent}}'."),
    EVENT_HTTP_EXECUTION("Dispatching event to \n URL '{{url}}' \n params '{{params}}'."),
    SETTINGS_FILE_PROCESSED("Settings file processed successfully."),
    SDK_INITIALIZED("SDK properly initialized.");

    private final String msg;

    DEBUG_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Pair<String, String>... pairs) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, pairs);
    }
  }

  public enum INFO_MESSAGES {
    NO_VARIATION_ALLOCATED("UserId '{{userId}' of campaign {{campaignTestKey}} did not get any variation."),
    NO_DATA_USER_PROFILE_SERVICE("Unable to fetch data from UserProfileService."),
    INITIATING_ACTIVATE("Initiating activation of user '{{userId}}' for campaign '{{campaignTestKey}}'."),
    INITIATING_GET_VARIATION("Initiating getVariation of user '{{userId}}' for campaign '{{campaignTestKey}}'."),
    GOT_VARIATION_FOR_USER("User '{{userId}}' of campaign '{{campaignTestKey}}' got variation '{{variation}}'."),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignTestKey}}'"),
    TRACK_API_VARIATION_NOT_FOUND("Variation not found for campaign '{{campaignTestKey}}' and userId '{{userId}'.");

    private final String msg;

    INFO_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Pair<String, String>... pairs) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, pairs);
    }
  }

  public enum WARNING_MESSAGES {
    INVALID_USER_PROFILE_MAP("The user profile service returned an invalid map: {{map}}."),
    NO_DATA_IN_USER_PROFILE("Data not found. Will proceed without UserProfileService.\n Variation might be different if configuration is changed."),
    CLOSE_GENERIC_CONNECTION("Unexpected exception on trying to close");

    private final String msg;

    WARNING_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Pair<String, String>... pairs) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, pairs);
    }
  }

  public enum ERROR_MESSAGES {
    GENERIC_ERROR("Unexpected Exception"),
    MISSING_IMPORT_SETTINGS_MANDATORY_PARAMS("AccountId and sdkKey are required for fetching account settings. Aborting!"),
    MISSING_CAMPAIGN_KEY("The campaignTestKey is required to get variation or track goal. Cannot proceed further."),
    MISSING_USER_ID("The userId is required to get variation or track goal. Cannot proceed further."),
    MISSING_GOAL_IDENTIFIER("The goal identifier is required to track a goal. Cannot proceed further."),
    MISSING_GOAL_REVENUE("Revenue value should be passed for revenue goal '{{goalIdentifier}}' for campaign '{{campaignTestKey}}' and userId '{{userId}}'."),
    MISSING_PROJECT_CONFIG("Not a valid VWO Instance. Cannot proceed further."),
    CAMPAIGN_NOT_FOUND("Unable to find the campaign. Please verify the Campaign Test Key."),
    CAMPAIGN_NOT_RUNNING("Campaign {{campaignTestKey}} is not RUNNING. Please verify from VWO App."),
    SAVE_USER_PROFILE_SERVICE_FAILED("Saving data into UserProfileService failed for user '{{userId}}'."),
    UNABLE_TO_DISPATCH_EVENT("Exception while executing dispatcher event."),
    CLOSE_HTTP_CONNECTION("Exception while closing http event handler."),
    CLOSE_EXECUTOR_SERVICE("Some problem while shutting down executor."),
    TRACK_API_GOAL_NOT_FOUND("Goal not found for campaign '{{campaignTestKey}}' and userId '{{userId}}'.");


    private final String msg;

    ERROR_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Pair<String, String>... pairs) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, pairs);
    }
  }

}



