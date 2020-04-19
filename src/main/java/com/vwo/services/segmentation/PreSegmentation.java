/**
 * Copyright 2019-2020 Wingify Software Pvt. Ltd.
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.segmentation.enums.OperatorEnum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PreSegmentation {

  private static final Logger LOGGER = Logger.getLogger(PreSegmentation.class);


  /**
   * Evaluates if the user comes under the pre segmentation of not.
   *
   * @param dsl - Segmentor DSl String
   * @param customVariables - User config values
   * @param userId - User ID
   * @param campaignKey - Campaign Key name
   * @return - Boolean result
   */
  public static boolean isPresegmentValid(Object dsl, Map<String, ?> customVariables, String userId, String campaignKey) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode dslNodes = dsl instanceof String ? mapper.readValue(dsl.toString(), JsonNode.class) : mapper.valueToTree(dsl);
      return traverseDslNodesPostOrder(dslNodes, customVariables);
    } catch (Exception e) {
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.SEGMENTATION_ERROR.value(new HashMap<String, String>() {
        {
          put("userId", userId);
          put("campaignKey", campaignKey);
          put("variation", userId);
          put("err", e.getMessage());
          put("customVariables", customVariables.toString());
        }
      }));
      return false;
    }
  }

  private static boolean traverseDslNodesPostOrder(JsonNode node, Map<String, ?> customVariables) {
    Iterator<Map.Entry<String, JsonNode>> dslFields = node.fields();

    if (dslFields.hasNext()) {
      Map.Entry<String, JsonNode> dslEntry = dslFields.next();
      JsonNode dslValue = dslEntry.getValue();
      String dslKey = dslEntry.getKey();

      if (SegmentOperator.isOperator(dslKey)) {
        SegmentOperator operatorInstance = new SegmentOperator(dslKey);

        if (operatorInstance.getOperator() == OperatorEnum.OR.value()) {
          return some(dslValue, customVariables);
        } else if (operatorInstance.getOperator() == OperatorEnum.AND.value()) {
          return every(dslValue, customVariables);
        } else {
          return operatorInstance.evaluate(traverseDslNodesPostOrder(dslValue, customVariables));
        }
      } else {
        return new SegmentOperand(dslKey, dslValue).evaluate(customVariables);
      }
    }

    return false;
  }

  private static boolean some(JsonNode nodes, Map<String, ?> customVariables) {
    for (JsonNode childNode: nodes) {
      if (traverseDslNodesPostOrder(childNode, customVariables)) {
        return true;
      }
    }

    return false;
  }

  private static boolean every(JsonNode nodes, Map<String, ?> customVariables) {
    for (JsonNode childNode: nodes) {
      if (!traverseDslNodesPostOrder(childNode, customVariables)) {
        return false;
      }
    }

    return true;
  }
}
