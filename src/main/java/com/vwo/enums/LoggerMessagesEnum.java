/**
 * Copyright 2019 Wingify Software Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vwo.enums;

import java.util.Map;

public class LoggerMessagesEnum {

  public static String getComputedMsg(String msg, Map<String, String>... maps) {
    for (Map<String, String> map: maps) {
      for (Map.Entry<String, String> pair : map.entrySet()) {
        String key = pair.getKey();
        String value = pair.getValue();
        msg = msg.replaceAll("\\{\\{" + key + "}}", value);
      }
    }
    return msg;
  }

  public enum DEBUG_MESSAGES {
    FETCHING_ACCOUNT_SETTINGS("Fetching  account settings for ID: {{accountID}}."),
    CAMPAIGN_KEY_FOUND("CampaignTestKey found: '{{campaignTestKey}}'."),
    UUID_GENERATED("Uuid generated for userId '{{userId}}' and accountId '{{accountId}}' is '{{uuid}}'."),
    INVALID_EVENT_QUEUE_SIZE("Event queue size should be greater than 0. Setting to default: {{eventQueueSize}}"),
    INVALID_EVENT_POOL_SIZE("Event pool size should be greater than 0. Setting to default: {{corePoolSize}}"),
    ACTIVATING_CAMPAIGN("Activating user '{{userId}}'  for variation '{{variation}}'."),
    GOT_STORED_VARIATION("Got stored variation '{{variationName}}' of campaign '{{campaignTestKey}}' for userId '{{userId}}' from UserProfileService."),
    NO_STORED_VARIATION("No stored variation for UserId '{{userId}}' for Campaign '{{campaignTestKey}}' found in UserProfileService."),
    NO_USER_PROFILE_DEFINED("UserProfileService is not defined. Skipping it!"),
    VARIATION_HASH_BUCKET_VALUE(
"Evaluated bucket value '{{bucketValue}}' of user {{userId}} for campaign '{{campaignTestKey}}' having hash value '{{hashValue}}' and traffic allocation '{{traffic}}'."
    ),
    USER_HASH_BUCKET_VALUE("User '{{userId}}' having hash '{{hashValue}}' got bucket value '{{bucketValue}}'"),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignTestKey}}'."),
    SAVED_IN_USER_PROFILE_SERVICE("Successfully saved variation '{{variation}}' of user {{userId}} in UserProfileService."),
    TRACK_USER_IMPRESSION_CREATED("Impression event built for track-user '{{userId}}' - '{{impressionEvent}}'."),
    TRACK_GOAL_IMPRESSION_CREATED("Impression event built for track-goal of user '{{userId}}' -  '{{goalEvent}}'."),
    EVENT_HTTP_EXECUTION("Dispatching event to \n URL: '{{url}}'."),
    SETTINGS_FILE_PROCESSED("Settings file processed successfully."),
    SDK_INITIALIZED("SDK properly initialized."),
    HTTP_RESPONSE("Got Http response {{response}}.");

    private final String msg;

    DEBUG_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, maps);
    }
  }

  public enum INFO_MESSAGES {
    NO_VARIATION_ALLOCATED("UserId '{{userId}}' of campaign {{campaignTestKey}} did not get any variation."),
    VARIATION_ALLOCATED_SUCCESSFULLY("Campaign '{{campaignTestKey}}' having variation '{{variation}}' with weight '{{weight}}' got range as: ({{startRange}} to {{endRange}})"),
    NO_DATA_USER_PROFILE_SERVICE("Unable to fetch data from UserProfileService."),
    INITIATING_ACTIVATE("Initiating activation of user '{{userId}}' for campaign '{{campaignTestKey}}'."),
    INITIATING_GET_VARIATION("Initiating getVariation of user '{{userId}}' for campaign '{{campaignTestKey}}'."),
    GOT_VARIATION_FOR_USER("User '{{userId}}' of campaign '{{campaignTestKey}}' got variation '{{variation}}'."),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignTestKey}}'"),
    TRACK_API_VARIATION_NOT_FOUND("Variation not found for campaign '{{campaignTestKey}}' and userId '{{userId}}'.");

    private final String msg;

    INFO_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, maps);
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

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, maps);
    }
  }

  public enum ERROR_MESSAGES {
    GENERIC_ERROR("Unexpected Exception"),
    MISSING_IMPORT_SETTINGS_MANDATORY_PARAMS("AccountId and sdkKey are required for fetching account settings. Aborting!"),
    MISSING_CAMPAIGN_KEY("The campaignTestKey is required to get variation or track goal. Cannot proceed further."),
    MISSING_USER_ID("The userId is required to get variation or track goal. Cannot proceed further."),
    MISSING_GOAL_IDENTIFIER("The goal identifier is required to track a goal. Cannot proceed further."),
    MISSING_GOAL_REVENUE("Revenue value should be passed for revenue goal '{{goalIdentifier}}' for campaign '{{campaignTestKey}}' and userId '{{userId}}'."),
    MISSING_PROJECT_CONFIG("No campaigns found or settings file is corrupted for campaign '{{campaignTestKey}}' and userId '{{userId}}'."),
    ACCOUNT_SETTINGS_NOT_FOUND("Request failed for fetching account settings. Got Status Code: '{{statusCode}}' and message: '{{message}}'."),
    CAMPAIGN_NOT_FOUND("Unable to find the campaign. Please verify the Campaign Test Key."),
    CAMPAIGN_NOT_RUNNING("Campaign {{campaignTestKey}} is not RUNNING. Please verify from VWO App."),
    SAVE_USER_PROFILE_SERVICE_FAILED("Saving data into UserProfileService failed for user '{{userId}}'."),
    UNABLE_TO_DISPATCH_EVENT("Exception while executing dispatcher event."),
    CLOSE_HTTP_CONNECTION("Exception while closing http event handler."),
    CLOSE_EXECUTOR_SERVICE("Some problem while shutting down executor."),
    TRACK_API_GOAL_NOT_FOUND("Goal '{{goalIdentifier}}' not found for campaign '{{campaignTestKey}}' and userId '{{userId}}'."),
    EVENT_DISPATCHER_EXCEPTION("Exception in Event Dispatcher."),
    URI_PARSER_EXCEPTION("URI Parsing Exception.");


    private final String msg;

    ERROR_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnum.getComputedMsg(this.msg, maps);
    }
  }

}
