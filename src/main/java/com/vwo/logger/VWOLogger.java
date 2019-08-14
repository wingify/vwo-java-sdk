package com.vwo.logger;

public interface VWOLogger {
  void trace(String var1, Object... var2);

  void debug(String var1, Object... var2);

  void info(String var1, Object... var2);

  void warn(String var1, Object... var2);

  void error(String var1, Object... var2);
}
