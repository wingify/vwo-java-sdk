package com.vwo.enums;

public class VWOEnums {

  public enum LOGGER_LEVEL {
    TRACE("trace"),
    DEBUG("debug"),
    INFO("info"),
    WARN("warn"),
    ERROR("error");

    private final String level;

    LOGGER_LEVEL(String level) {
      this.level = level;
    }

    public String value() {
      return this.level;
    }
  }
}



