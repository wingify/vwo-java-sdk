package com.vwo.logger;

import com.vwo.VWO;

public abstract class VWOLogger {
  public static String level;

  public VWOLogger() {
    this.level = VWO.Enums.LOGGER_LEVEL.ERROR.value();
  }

  public VWOLogger(String level) {
    this.level = level;
  }

  public abstract void trace(String var1, Object... var2);

  public abstract void debug(String var1, Object... var2);

  public abstract void info(String var1, Object... var2);

  public abstract void warn(String var1, Object... var2);

  public abstract void error(String var1, Object... var2);
}


