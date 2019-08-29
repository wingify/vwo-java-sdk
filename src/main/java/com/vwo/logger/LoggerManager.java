package com.vwo.logger;

import com.vwo.VWO;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerManager {
  private Logger logger;
  private static VWOLogger customLogger;
  private static ArrayList<String> logPriority = new ArrayList<>();

  {
    logPriority.add(VWO.Enums.LOGGER_LEVEL.TRACE.value());
    logPriority.add(VWO.Enums.LOGGER_LEVEL.DEBUG.value());
    logPriority.add(VWO.Enums.LOGGER_LEVEL.INFO.value());
    logPriority.add(VWO.Enums.LOGGER_LEVEL.WARN.value());
    logPriority.add(VWO.Enums.LOGGER_LEVEL.ERROR.value());
  }

  private LoggerManager(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  private static boolean shouldAllowLogLevel(String level) {
    if (LoggerManager.customLogger != null) {
      return logPriority.indexOf(LoggerManager.customLogger.level) <= logPriority.indexOf(level);
    }

    return true;
  }

  public static void init(VWOLogger customLogger) {
    if (customLogger != null && logPriority.indexOf(customLogger.level) == -1) {
      customLogger.level = VWO.Enums.LOGGER_LEVEL.ERROR.value();
    }
    LoggerManager.customLogger = customLogger;
  }

  public static LoggerManager getLogger(Class<?> clazz) {
    return new LoggerManager(clazz);
  }

  public void trace(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.TRACE.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.trace(var1);
      } else {
        this.logger.trace(var1);
      }
    }
  }

  public void trace(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.TRACE.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.trace(var1, var2);
      } else {
        this.logger.trace(var1, var2);
      }
    }
  }

  public void debug(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.DEBUG.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.debug(var1);
      } else {
        this.logger.debug(var1);
      }
    }
  }


  public void debug(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.DEBUG.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.debug(var1, var2);
      } else {
        this.logger.debug(var1, var2);
      }
    }
  }

  public void info(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.INFO.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.info(var1);
      } else {
        this.logger.info(var1);
      }
    }
  }

  public void info(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.INFO.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.info(var1, var2);
      } else {
        this.logger.info(var1, var2);
      }
    }
  }

  public void warn(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.WARN.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.warn(var1);
      } else {
        this.logger.warn(var1);
      }
    }
  }

  public void warn(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.WARN.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.warn(var1, var2);
      } else {
        this.logger.warn(var1, var2);
      }
    }
  }

  public void error(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.ERROR.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.error(var1);
      } else {
        this.logger.error(var1);
      }
    }
  }

  public void error(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.ERROR.value())) {
      if (LoggerManager.customLogger != null) {
        LoggerManager.customLogger.error(var1, var2);
      } else {
        this.logger.error(var1, var2);
      }
    }
  }
}