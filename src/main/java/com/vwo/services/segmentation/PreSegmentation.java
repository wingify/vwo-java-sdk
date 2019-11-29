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

package com.vwo.services.segmentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.services.segmentation.enums.OperatorEnum;

import java.util.Iterator;
import java.util.Map;

public class PreSegmentation {

  /**
   * Evaluates if the user comes under the pre segmentation of not.
   *
   * @param dsl - Segmentor DSl String
   * @param customVariables - User config values
   * @return - Boolean result
   */
  public static boolean isPresegmentValid(Object dsl, Map<String, ?> customVariables) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode dslNodes = dsl instanceof String ? mapper.readValue(dsl.toString(), JsonNode.class) : mapper.valueToTree(dsl);
      return traverseDslNodesPostOrder(dslNodes, customVariables);
    } catch (Exception e) {
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
