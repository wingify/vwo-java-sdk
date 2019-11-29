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

import com.vwo.logger.Logger;

import java.util.Map;

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
    GET_SETTINGS_IMPRESSION_CREATED("Impression event built for get settings '{{requestParams}}'."),
    TRACK_USER_IMPRESSION_CREATED("Impression event built for track-user '{{userId}}' - '{{requestParams}}'."),
    TRACK_GOAL_IMPRESSION_CREATED("Impression event built for track-goal of user '{{userId}}' -  '{{requestParams}}'."),
    POST_SEGMENTATION_REQUEST_CREATED("Request params build for post segmentation of user '{{userId}}' -  '{{requestParams}}'."),
    HTTP_REQUEST_EXECUTED("Dispatching request to \n URL: '{{url}}'."),
    SETTINGS_FILE_PROCESSED("Settings file processed successfully."),
    SDK_INITIALIZED("SDK properly initialized."),
    HTTP_RESPONSE("Got Http response {{response}}."),

    // Pre Segmentation
    NO_PRE_SEGMENT_FOUND("Pre segment condition(s) not found for campaign '{{campaignKey}}'. Skipping it.");

    private final String msg;

    DEBUG_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnums.getComputedMsg(this.msg, maps);
    }
  }

  public enum INFO_MESSAGES {
    NO_VARIATION_ALLOCATED("UserId '{{userId}}' of campaign '{{campaignKey}}' did not get any variation."),
    VARIATION_ALLOCATED_SUCCESSFULLY("Campaign '{{campaignKey}}' having variation '{{variation}}' with weight '{{weight}}' got range as: ({{startRange}} to {{endRange}})"),
    NO_DATA_USER_STORAGE_SERVICE("Unable to fetch data from user storage."),
    INITIATING_ACTIVATE("Initiating activation of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_GET_VARIATION("Initiating getVariation of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_GET_FEATURE_VARIATION("Initiating getFeatureVariable for variable key '{{variableKey}}' of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_IS_FEATURE_ENABLED("Initiating isFeatureEnabled of user '{{userId}}' for campaign '{{campaignKey}}'."),
    INITIATING_PUSH_DIMENSION("Initiating push segment of user '{{userId}}' with tag name `{{tagKey}}` and tag value '{{tagValue}}'."),
    GOT_VARIATION_FOR_USER("User '{{userId}}' of campaign '{{campaignKey}}' got variation '{{variation}}'."),
    USER_NOT_PART_OF_CAMPAIGN("User '{{userId}}' did not become part of campaign '{{campaignKey}}'"),
    TRACK_API_VARIATION_NOT_FOUND("Variation not found for campaign '{{campaignKey}}' and userId '{{userId}}'."),
    FEATURE_NOT_ENABLED("isFeatureEnabled flag is false for variation '{{variation}}', hence retrieving variable from Control"),
    FEATURE_VARIABLE_FOUND("Value for variable '{{variableKey}}' of campaign '{{campaignKey}}' for user '{{userId}}' is: '{{variableValue}}'."),

    // Pre Segmentation
    PRE_SEGMENT_SATISFIED("User '{{userId}}' satisfied the pre segment condition(s) for campaign '{{campaignKey}}' and customVariables '{{CustomVariables}}'."),
    PRE_SEGMENT_NOT_SATISFIED("User '{{userId}}' didn't satisfy the pre segment condition(s) for campaign '{{campaignKey}}' and customVariables '{{CustomVariables}}'."),
    VALUE_PASSED_FOR_NO_PRE_SEGMENT("Pre-segment customVariables are passed for campaign '{{campaignKey}}' and userId '{{userId}}' but campaign has no pre-segmentation."),
    VALUE_NOT_PASSED_FOR_PRE_SEGMENT("Pre-segment customVariables are not passed for campaign '{{campaignKey}}' and userId '{{userId}}' but campaign has pre-segmentation.");

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

    // Pre Segmentation
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
    MISSING_IMPORT_SETTINGS_MANDATORY_PARAMS("AccountId and sdkKey are required for fetching account settings. Aborting!"),
    MISSING_CAMPAIGN_KEY("The campaignKey is required for '{{api}}' api. Cannot proceed further."),
    MISSING_USER_ID("The userId is required for '{{api}}' api. Cannot proceed further."),
    MISSING_VARIABLE_KEY("The variableKey is required to get feature variable."),
    MISSING_TAG_KEY("A tag-key is required for the push API."),
    TAG_KEY_LENGTH_EXCEEDED("Length of tag key '{{tagKey}}' and userId '{{userId}}' can not be greater than 255."),
    TAG_VALUE_LENGTH_EXCEEDED("Length of tag value '{{tagValue}}' for tag key '{{tagKey}}' and userId '{{userId}}' can not be greater than 255."),
    MISSING_TAG_VALUE("A tag-value is required for the push API."),
    MISSING_GOAL_IDENTIFIER("The goal identifier is required to track a goal. Cannot proceed further."),
    MISSING_GOAL_REVENUE("Revenue value should be passed for revenue goal '{{goalIdentifier}}' for campaign '{{campaignKey}}' and userId '{{userId}}'."),
    MISSING_PARAM("Expected parameters are missing."),
    ACCOUNT_SETTINGS_NOT_FOUND("Request failed for fetching account settings. Got Status Code: '{{statusCode}}' and message: '{{message}}'."),
    CAMPAIGN_NOT_FOUND("Campaign {{campaignKey}} is not RUNNING. Please verify from VWO App."),
    SAVE_USER_STORAGE_SERVICE_FAILED("Saving data into user storage failed for user '{{userId}}'."),
    UNABLE_TO_DISPATCH_HTTP_REQUEST("Exception while executing http request."),
    TRACK_API_GOAL_NOT_FOUND("Goal '{{goalIdentifier}}' not found for campaign '{{campaignKey}}' and userId '{{userId}}'."),
    HTTP_REQUEST_EXCEPTION("Exception occurred in Http request."),
    INVALID_API("API '{{api}}' is not valid for userId '{{userId}}' in campaign '{{campaignKey}}' having campaign type '{{campaignType}}'."),
    VARIABLE_NOT_FOUND("Variable '{{variableKey}}' not found for campaign '{{campaignKey}}' and type '{{campaignType}}' for userId '{{userId}}'."),
    VARIABLE_REQUESTED_WITH_WRONG_TYPE("Got variable type as '{{variableType}}', but expected '{{expectedVariableType}}. Please read docs and use correct API. Returning null.");


    private final String msg;

    ERROR_MESSAGES(String msg) {
      this.msg = msg;
    }

    public String value(Map<String, String>... maps) {
      return LoggerMessagesEnums.getComputedMsg(this.msg, maps);
    }
  }
}
