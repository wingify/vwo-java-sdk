/**
 * Copyright 2019-2021 Wingify Software Pvt. Ltd.
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

import com.vwo.logger.Logger;

import java.util.Map;

@SuppressWarnings("checkstyle:LineLength")
public class LoggerMessagesEnums {

  private static final Logger LOGGER = Logger.getLogger(LoggerMessagesEnums.class);

  public static String getComputedMsg(String msg, Map<String, String>... maps) {
    for (Map<String, String> map: maps) {
      for (Map.Entry<String, String> pair : map.entrySet()) {
        String key = pair.getKey();
        String value = pair.getValue();
        try {
          msg = msg.replaceAll("\\{\\{" + key + "}}", value.replaceAll("\\$", "\\\\\\$"));
        } catch (Exception e) {
          LOGGER.error("Exception occurred while logging the value: " + value);
        }
      }
    }
    return msg;
  }

  public enum DEBUG_MESSAGES {
    FETCHING_ACCOUNT_SETTINGS("Fetching  account settings for ID: {{accountID}}."),
    REGISTERED_POLLING_INTERVAL("Polling of settings-file is registered with a periodic interval of {{pollingInterval}}ms."),
    VARIATION_RANGE_ALLOCATED("Variation '{{variation}}' with weight '{{weight}}' got range as: ({{startRange}} to {{endRange}})"),
    CAMPAIGN_KEY_FOUND("campaignKey found: '{{campaignKey}}'."),
    UUID_GENERATED("Uuid generated for userId '{{userId}}' and accountId '{{accountId}}' is '{{uuid}}'."),
    ACTIVATING_CAMPAIGN("Activating user '{{userId}}'  for variation '{{variation}}'."),
    GOT_STORED_VARIATION("Got stored variation '{{variationName}}' of campaign '{{campaignKey}}' for userId '{{userId}}' from user storage."),
    NO_STORED_VARIATION("No stored variation for UserId '{{userId}}' for Campaign '{{campaignKey}}' found in user storage."),
    NO_USER_STORAGE_DEFINED("User storage is not defined. Skipping it!"),
    VARIATION_HASH_VALUE(
"Evaluated bucket value '{{variationHashValue}}' of user {{userId}} for campaign '{{campaignKey}}' having hash value '{{hashValue}}' and traffic allocation '{{traffic}}'."
    ),
    USER_HASH_BUCKET_VALUE("User '{{userId}}' having hash '{{hashValue}}' got bucket value '{{bucketValue}}'"),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignKey}}'."),
    SAVED_IN_USER_STORAGE_SERVICE("Successfully saved variation '{{variation}}' of user {{userId}} in user storage."),
    GET_SETTINGS_IMPRESSION_CREATED("Impression event built for get settings."),
    TRACK_USER_IMPRESSION_CREATED("Impression event built for track-user '{{userId}}'."),
    TRACK_GOAL_IMPRESSION_CREATED("Impression event built for track-goal of user '{{userId}}'."),
    POST_SEGMENTATION_REQUEST_CREATED("Request params build for post segmentation of user '{{userId}}'."),
    HTTP_REQUEST_EXECUTED("Dispatching request to \n URL: '{{url}}'."),
    SETTINGS_FILE_PROCESSED("Settings file processed successfully."),
    SDK_INITIALIZED("SDK properly initialized."),
    HTTP_RESPONSE("Got Http response {{response}}."),
    GOAL_SHOULD_TRACK_AGAIN("Goal:{{goalIdentifer}} of Campaign:{{campaignKey}} for User ID:{{userId}} has already been tracked earlier. shouldTrackReturningUser is true, therefore, is eligible to be tracked again"),

    // Segmentation
    SEGMENTATION_STATUS("For userId:{{userId}} of campaign:{{campaignKey}} with variables:{{customVariables}} {{status}} {{segmentationType}} for {{variation}}"),
    SEGMENTATION_SKIPPED("For userId:{{userId}} of campaign:{{campaignKey}}, segment was missing, hence skipping segmentation for {{variation}}"),
    WHITELISTING_SKIPPED("For userId:{{userId}} of campaign:{{campaignKey}}, whitelisting was skipped"),
    BEFORE_FLUSHING("Flushing events queue {{manually}} having {{length}} events for account:{{accountId}}. {{timer}}, queue summary: {{queue_metadata}}"),
    BATCH_EVENT_LIMIT_EXCEEDED("Impression event - {{endPoint}} failed due to exceeding payload size. Parameter eventsPerRequest in batchEvents config in launch API has value:{{eventsPerRequest}} for accountId:{{accountId}}. Please read the official documentation for knowing the size limits."),
    BULK_NOT_PROCESSED("Batch events couldn't be received by VWO. Calling Flush Callback with error and data."),
    NO_BATCH_QUEUE("No batch queue present for account:{{accountId}} when calling flushEvents API. Check batchEvents config in launch API"),
    CAMPAIGN_NOT_ACTIVATED("Campaign:{{campaignKey}} for User ID:{{userId}} is not yet activated for API:{{api}}. Use activate API to activate A/B test or isFeatureEnabled API to activate Feature Test."),
    EVENT_QUEUE_EMPTY("Event Batching queue is empty"),
    EVENTS_PER_REQUEST_OUT_OF_BOUNDS("eventsPerRequest should be > {{min_value}} and <= {{max_value}}. Assigning it the default value i.e {{default_value}}"),
    REQUEST_TIME_INTERVAL_OUT_OF_BOUNDS("requestTimeInterval should be > {{min_value}}. Assigning it the default value i.e {{default_value}} seconds"),
    GOT_ELIGIBLE_CAMPAIGNS("Campaigns:{{eligibleCampaignsKey}} are eligible, campaigns: {{nonEligibleCampaignsKey}} are not eligible from the Group:{{groupName}} for the User Id:{{userId}}");

    private final String msg;

    DEBUG_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnums.getComputedMsg(this.msg, maps);
    }
  }

  public enum INFO_MESSAGES {
    POLLED_SETTINGS_FILE("Settings-file fetched successfully via polling for the accountId: {{accountID}}"),
    VWO_SDK_INSTANCE_UPDATED("vwo-sdk instance is updated with the latest settings-file for the accountId: {{accountID}}"),
    NO_VARIATION_ALLOCATED("UserId '{{userId}}' of campaign '{{campaignKey}}' did not get any variation."),
    NO_DATA_USER_STORAGE_SERVICE("Unable to fetch data from user storage."),
    INITIATING_ACTIVATE("Initiating activation of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_GET_VARIATION("Initiating getVariation of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_GET_FEATURE_VARIATION("Initiating getFeatureVariable for variable key '{{variableKey}}' of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_IS_FEATURE_ENABLED("Initiating isFeatureEnabled of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_PUSH_DIMENSION("Initiating push segment of user '{{userId}}' with tag name `{{tagKey}}` and tag value '{{tagValue}}'."),
    GOT_VARIATION_FOR_USER("User '{{userId}}' of campaign '{{campaignKey}}' got variation '{{variation}}'."),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignKey}}'"),
    FEATURE_NOT_ENABLED("isFeatureEnabled flag is false for variation '{{variation}}', hence retrieving variable from Control"),
    FEATURE_VARIABLE_FOUND("Value for variable '{{variableKey}}' of campaign '{{campaignKey}}' for user '{{userId}}' is: '{{variableValue}}'."),
    GOAL_ALREADY_TRACKED("Goal:{{goalIdentifer}} of Campaign:{{campaignKey}} for User ID:{{userId}} has already been tracked earlier. Skipping now."),
    USER_ALREADY_TRACKED("User ID:{{userId}} for Campaign:{{campaignKey}} has already been tracked earlier for {{api}} API. Skipping now."),
    SETTINGS_NOT_UPDATED("Settings-file fetched are same as earlier fetched settings"),
    AFTER_FLUSHING("Events queue having {{length}} events has been flushed {{manually}}, queue summary: {{queue_metadata}}"),

    // Segmentation
    SEGMENTATION_STATUS("UserId:{{userId}} of campaign:{{campaignKey}} with variables:{{customVariables}} {{status}} {{segmentationType}} for {{variation}}"),
    BULK_IMPRESSION_SUCCESS("Impression event - {{endPoint}} was successfully received by VWO having accountId:{{a}}"),
    CAMPAIGN_NOT_ACTIVATED("Activate the campaign:{{campaignKey}} for User ID:{{userId}} to {{reason}}."),
    IMPRESSION_SUCCESS_QUEUE("Impression event was successfully pushed in queue"),
    GOT_WINNER_CAMPAIGN("Campaign:{{campaignKey}} is selected from the mutually exclusive group:{{groupName}} for the User ID:{{userId}}."),
    GOT_ELIGIBLE_CAMPAIGNS("Got {{noOfEligibleCampaigns}} eligible winners out of {{noOfGroupCampaigns}} campaigns from the Group:{{groupName}} and for User ID:{{userId}}"),
    CALLED_CAMPAIGN_NOT_WINNER("Campaign:{{campaignKey}} does not qualify from the mutually exclusive group:{{groupName}} for User ID:{{userId}}");

    private final String msg;

    INFO_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnums.getComputedMsg(this.msg, maps);
    }
  }

  public enum WARNING_MESSAGES {
    INVALID_USER_STORAGE_MAP("The user storage service returned an invalid map: {{map}}."),
    NO_DATA_IN_USER_STORAGE("Data not found. Will proceed without user storage.\n Variation might be different if configuration is changed."),

    // Segmentation
    INVALID_OPERATOR_EVALUATION("Invalid operator evaluation, expected operator(s) '{{expectedOperator}}' but got '{{operator}}' with operand(s) '{{operands}}"),
    INVALID_OPERAND_NODE("Invalid operand node, should contain at least 1 nested child but got '{{node}}'"),
    INVALID_OPERAND_TYPE("Invalid operand type '{{operand}}'"),
    OPERAND_MATCHING_FAILURE("Unable to match the operand value '{{operand}}' with the expected value '{{expectedOperand}}'. Please check if the expected type is valid.");


    private final String msg;

    WARNING_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnums.getComputedMsg(this.msg, maps);
    }
  }

  public enum ERROR_MESSAGES {
    GENERIC_ERROR("Unexpected Exception"),
    MISSING_POLLING_SDK_KEY("sdkKey is required along with pollingInterval to poll the settings-file"),
    MISSING_IMPORT_SETTINGS_MANDATORY_PARAMS("AccountId and sdkKey are required for fetching account settings. Aborting!"),
    SETTINGS_FILE_POLLING_ERROR("Failed fetching of Settings-file via polling for the accountId: {{accountID}}"),
    ACTIVATE_API_MISSING_PARAMS("'activate' API got bad parameters. It expects campaignKey(String) as first, userId(String) as second and options(optional Object) as third argument"),
    GET_VARIATION_API_MISSING_PARAMS("'getVariation' API got bad parameters. It expects campaignKey(String) as first, userId(String) as second and options(optional Object) as third argument"),
    IS_FEATURE_ENABLED_API_MISSING_PARAMS("'isFeatureEnabled' API got bad parameters. It expects Campaign(String) as first, userId(String) as second and options(optional Object) as third argument"),
    GET_FEATURE_VARIABLE_MISSING_PARAMS(
      "'getFeatureVariableValue' API got bad parameters. It expects campaignKey(String) as first, variableKey(String) as second, userId(String) as third, and options(optional Object) as fourth argument"
    ),
    PUSH_API_INVALID_PARAMS("'push' API got bad parameters. It expects tagKey(String) as first, tagValue(String) as second and userId(String) as third argument"),
    TRACK_API_MISSING_PARAMS(
      "'track' API got bad parameters. It expects campaignKey(String or Array of Strings or null) as first, userId(String) as second, goalIdentifier(String/Number) as third and options(optional Object) as fourth argument"
    ),
    TAG_KEY_LENGTH_EXCEEDED("Length of tag key '{{tagKey}}' and userId '{{userId}}' can not be greater than 255."),
    TAG_VALUE_LENGTH_EXCEEDED("Length of tag value '{{tagValue}}' for tag key '{{tagKey}}' and userId '{{userId}}' can not be greater than 255."),
    MISSING_GOAL_REVENUE("Revenue value should be passed for revenue goal '{{goalIdentifier}}' for campaign '{{campaignKey}}' and userId '{{userId}}'."),
    MISSING_PARAM("Expected parameters are missing."),
    ACCOUNT_SETTINGS_NOT_FOUND("Request failed for fetching account settings. Got Status Code: '{{statusCode}}' and message: '{{message}}'."),
    CAMPAIGN_NOT_FOUND("Campaign {{campaignKey}} is not RUNNING. Please verify from VWO App."),
    NO_CAMPAIGN_FOUND("No campaign found for goalIdentifier:{{goalIdentifier}}. Please verify from VWO app."),
    SAVE_USER_STORAGE_SERVICE_FAILED("Saving data into user storage failed for user '{{userId}}'."),
    UNABLE_TO_DISPATCH_HTTP_REQUEST("Exception while executing http request."),
    TRACK_API_GOAL_NOT_FOUND("Goal '{{goalIdentifier}}' not found for campaign '{{campaignKey}}' and userId '{{userId}}'."),
    HTTP_REQUEST_EXCEPTION("Exception occurred in Http request."),
    INVALID_API("API '{{api}}' is not valid for userId '{{userId}}' in campaign '{{campaignKey}}' having campaign type '{{campaignType}}'."),
    VARIABLE_NOT_FOUND("Variable '{{variableKey}}' not found for campaign '{{campaignKey}}' and type '{{campaignType}}' for userId '{{userId}}'."),
    VARIABLE_REQUESTED_WITH_WRONG_TYPE("Got variable type as '{{variableType}}', but expected '{{expectedVariableType}}. Please read docs and use correct API. Returning null."),
    SEGMENTATION_ERROR("Error while segmenting the userId:{{userId}} of campaignKey:{{campaignKey}} for {{variation}} with customVariables:{{customVariables}}. Error message: {{err}}"),
    IMPRESSION_FAILED("Impression event could not be sent to VWO - {{endPoint}}. Reason: {{err}}");


    private final String msg;

    ERROR_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnums.getComputedMsg(this.msg, maps);
    }
  }
}
