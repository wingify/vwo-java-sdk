/**
 * Copyright 2019-2022 Wingify Software Pvt. Ltd.
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

package com.vwo.services.segmentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.vwo.logger.Logger;
import com.vwo.logger.LoggerService;
import com.vwo.services.segmentation.enums.OperandEnum;
import com.vwo.services.segmentation.enums.OperandValueTypeEnum;
import com.vwo.services.segmentation.enums.VWOAttributesEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigDecimal;

public class SegmentOperand {
  private static final Logger LOGGER = Logger.getLogger(SegmentOperand.class);
  private static final ArrayList<String> expectedOperandValueTypes = new ArrayList<>(Arrays.asList(
      OperandValueTypeEnum.LOWER.value(),
      OperandValueTypeEnum.REGEX.value(),
      OperandValueTypeEnum.WILDCARD.value()
  ));
  private String operandType;
  private String operandKey;
  private String operandValue;

  //  Not in use for now.
  //  public SegmentOperand(String operandString) {
  //    try {
  //      JsonNode operand = (new ObjectMapper()).readValue(operandString, JsonNode.class);
  //      parseOperandNode(operand);
  //    } catch (Exception e) {
  //      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.INVALID_SEGMENT_DSL.value(new HashMap<String, String>() {
  //        {
  //          put("dsl", operandString);
  //        }
  //      }));
  //    }
  //  }
  //
  public SegmentOperand(JsonNode operandNode) {
    parseOperandNode(operandNode);
  }

  public SegmentOperand(String operandType, JsonNode operand) {
    this.operandType = operandType;
    parseOperandKeyValueNode(operand);
  }

  /**
   * Evaluates the operand value wrt user defined customVariables.
   *
   * @param customVariables - Key value pairs of user defined customVariables
   * @return boolean value stating operand value matched or not.
   */
  public boolean evaluate(Map<String, ?> customVariables) {
    try {
      if (OperandEnum.CUSTOM_VARIABLE.value().equalsIgnoreCase(this.operandType)) {
        Object rawValue = customVariables == null ? "" : customVariables.get(this.operandKey);
        String value = rawValue == null ? "" : rawValue.toString();
        return isCustomVariableMatching(value.trim(), this.operandValue.trim());
      } else if (OperandEnum.USER.value().equalsIgnoreCase(this.operandType)) {
        String value = (String) customVariables.get(VWOAttributesEnum.USER_ID.value());
        return isVariationTargetingVariableMatching(value, this.operandValue.trim());
      }

      return false;
    } catch (Exception e) {
      //      LOGGER.error(LoggerMessagesEnums.WARNING_MESSAGES.INVALID_OPERAND_TYPE.value(new HashMap<String, String>() {
      //        {
      //          put("operand", operandType);
      //        }
      //      }));
      return false;
    }
  }

  /**
   * Extracts operand type and key value pairs.
   *
   * @param node - {custom_variable: { key: "value" }}
   */
  private void parseOperandNode(JsonNode node) {
    Iterator<Map.Entry<String, JsonNode>> operandField = node.fields();
    if (operandField.hasNext()) {
      Map.Entry<String, JsonNode> operandEntry = operandField.next();
      this.operandType = operandEntry.getKey();
      parseOperandKeyValueNode(operandEntry.getValue());
    }

    //    LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.INVALID_OPERAND_NODE.value(new HashMap<String, String>() {
    //      {
    //        put("node", node.toString());
    //      }
    //    }));
  }

  /**
   * Extracts key value pair from operand.
   *
   * @param node - { key: "value" }
   */
  private void parseOperandKeyValueNode(JsonNode node) {
    Iterator<Map.Entry<String, JsonNode>> operandKeyValueField = node.fields();
    if (operandKeyValueField.hasNext()) {
      Map.Entry<String, JsonNode> operandKeyValueEntry = operandKeyValueField.next();
      this.operandKey = operandKeyValueEntry.getKey();
      this.operandValue = operandKeyValueEntry.getValue().textValue();
    } else {
      this.operandValue = node.textValue();
    }
  }

  /**
   * Matches the variation targeting variable value with the user attribute(s).
   *
   * @param userAttribute - User attribute to match
   * @param expectedValues - CSV to match from
   * @return - boolean value
   */
  private static boolean isVariationTargetingVariableMatching(String userAttribute, String expectedValues) {
    return Arrays.stream(expectedValues.split(",")).anyMatch(value -> userAttribute.equals(value.trim()));
  }

  /**
   * Matches the segment operand value with the one passed by the user.
   *
   * @param actualValue - Value passed by user
   * @param expectedValue - DSL value like wildcard(*1*)
   * @return - boolean value
   */
  private static boolean isCustomVariableMatching(String actualValue, String expectedValue) {
    try {
      Pattern regex = Pattern.compile("^(" + String.join("|", expectedOperandValueTypes) + ")\\((.*)\\)");
      Matcher matcher = regex.matcher(expectedValue);
      if (matcher.matches()) {
        String type = matcher.group(1);
        String value = matcher.group(2);

        if (type.equals(OperandValueTypeEnum.LOWER.value())) {
          return isMatchingValue(value.toLowerCase(), actualValue.toLowerCase());
        }

        if (type.equals(OperandValueTypeEnum.REGEX.value())) {
          return isMatchingRegex(value, actualValue);
        }

        // wildcard
        return isMatchingWildCard(actualValue, value);
      } else {
        // Equality match
        return isMatchingValue(expectedValue, actualValue);
      }
    } catch (Exception e) {
      //      LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.OPERAND_MATCHING_FAILURE.value(new HashMap<String, String>() {
      //        {
      //          put("operand", actualValue);
      //          put("expectedOperand", expectedValue);
      //        }
      //      }));

      return false;
    }
  }

  /**
   * Checks if the customVariables matches the wildcard value.
   * @param actualValue - Value passed by user
   * @param expectedValue - Wildcard value/regex
   * @return - boolean value
   */
  private static boolean isMatchingWildCard(String actualValue, String expectedValue) {
    if (expectedValue.startsWith("*") && expectedValue.endsWith("*")) {
      return isMatchingOperator("contains", actualValue, expectedValue.substring(1, expectedValue.length() - 1));
    }

    if (expectedValue.startsWith("*")) {
      return isMatchingOperator("endsWith", actualValue, expectedValue.substring(1));
    }

    if (expectedValue.endsWith("*")) {
      return isMatchingOperator("startsWith", actualValue, expectedValue.substring(0, expectedValue.length() - 1));
    }

    return isMatchingValue(actualValue, expectedValue);
  }

  /**
   * Checks if the value matches the regex.
   * Don't use .matches() -> https://stackoverflow.com/questions/17113276/java-regular-expression-on-matching-asterisk-only-when-it-is-the-last-character/17113363
   *
   * @param regexStr - Regex value in string
   * @param value - Value to match
   * @return - boolean value
   */
  private static boolean isMatchingRegex(String regexStr, String value) {
    try {
      return Pattern.compile(regexStr).matcher(value).find();

    } catch (Exception e) {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("SEGMENTATION_REGEX_CREATION_FAILED"), new HashMap<String, String>() {
        {
          put("regex", regexStr);
        }
      }));
      return false;
    }
  }

  /**
   * Matches whether the customVariables are equal or not considering type mismatch.
   *
   * @param actualValue - Value passed by user
   * @param expectedValue - Value expected for presegment to match
   * @return - boolean value
   */
  private static boolean isMatchingValue(String actualValue, String expectedValue) {
    try {
      double f1 = Double.valueOf(actualValue);
      double f2 = Double.valueOf(expectedValue);
      return f1 == f2;
    } catch (Exception e) {
      return actualValue.equals(expectedValue);
    }
  }

  /**
   * Matches whether the customVariables are equal or not considering type mismatch.
   *
   * @param actualValue - Value passed by user
   * @param expectedValue - Value expected for presegment to match
   * @return - boolean value
   */
  private static boolean isMatchingOperator(String operator, String actualValue, String expectedValue) {
    String normalizedActualValue = actualValue;
    String normalizedExpectedValue = expectedValue;

    try {
      // TODO: Find better way to convert to stringified double without exponential notation.
      normalizedActualValue = new BigDecimal(Double.toString(Double.valueOf(actualValue))).toPlainString();
      normalizedExpectedValue = new BigDecimal(Double.toString(Double.valueOf(expectedValue))).toPlainString();
    } catch (Exception e) {
      // Revert back previous value (safe check)
      normalizedActualValue = actualValue;
    }

    switch (operator.toLowerCase()) {
      case "endswith":
        return normalizedActualValue.endsWith(normalizedExpectedValue) || actualValue.endsWith(expectedValue);
      case "startswith":
        return normalizedActualValue.startsWith(normalizedExpectedValue) || actualValue.startsWith(expectedValue);
      default:
        // *123* matches 654123.2323 And also *123.0* matches 876123
        return normalizedActualValue.contains(normalizedExpectedValue) || actualValue.contains(expectedValue);
    }
  }
}
