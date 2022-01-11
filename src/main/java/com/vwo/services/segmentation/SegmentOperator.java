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

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.services.segmentation.enums.OperatorEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SegmentOperator {
  private static final Logger LOGGER = Logger.getLogger(SegmentOperator.class);
  private static final ArrayList<String> allowedOperators = new ArrayList<>(Arrays.asList(OperatorEnum.AND.value(), OperatorEnum.OR.value(), OperatorEnum.NOT.value()));
  private String operator;

  /**
   * Checks if the input key is of operator type or not.
   *
   * @param key - Key to check
   * @return boolean result
   */
  public static boolean isOperator(String key) {
    return allowedOperators.contains(key);
  }

  public SegmentOperator(String operator) {
    this.operator = operator;
  }

  public String getOperator() {
    return this.operator;
  }

  /**
   * Evaluates the operator value wrt given operand.
   *
   * @param operand - Boolean value of operand
   * @return - Boolean value of evaluated result
   */
  public boolean evaluate(boolean operand) {
    String operator = this.operator;
    if (operator.equalsIgnoreCase(OperatorEnum.NOT.value())) {
      return !operand;
    }

    LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.INVALID_OPERATOR_EVALUATION.value(new HashMap<String, String>() {
      {
        put("expectedOperator", OperatorEnum.NOT.value());
        put("operator", operator);
        put("operands", String.valueOf(operand));
      }
    }));
    return false;
  }

  // Not in use for now.
  /**
   * Evaluates the operator value wrt given multiple operands.
   *
   * @param operand1 - Boolean value of operand1
   * @param operand2 - Boolean value of operand2
   * @return - Boolean value of evaluated result
   */
  //  public boolean evaluate(boolean operand1, boolean operand2) {
  //    String operator = this.operator;
  //
  //    if (operator.equalsIgnoreCase(OperatorEnum.AND.value())) {
  //      return operand1 && operand2;
  //    } else if (operator.equalsIgnoreCase(OperatorEnum.OR.value())) {
  //      return operand1 || operand2;
  //    }  else if (operator.equalsIgnoreCase(OperatorEnum.NOT.value())) {
  //      return !operand1;
  //    }
  //
  //    LOGGER.warn(LoggerMessagesEnums.WARNING_MESSAGES.INVALID_OPERATOR_EVALUATION.value(new HashMap<String, String>() {
  //      {
  //        put("expectedOperator", String.join(", ", allowedOperators));
  //        put("operator", operator);
  //        put("operands", operand1 + ", " + operand2);
  //      }
  //    }));
  //    return false;
  //  }
}
