package com.vwo.enums;

public class GoalEnum {

  public enum GOAL_TYPES {
    REVENUE("REVENUE_TRACKING"),
    CUSTOM("CUSTOM_GOAL");

    private final String type;

    GOAL_TYPES(String type) {
      this.type = type;
    }

    public String value() {
      return this.type;
    }
  }
}
