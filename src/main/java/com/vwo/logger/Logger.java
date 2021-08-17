/**
 * Copyright 2019-2021 Wingify Software Pvt. Ltd.
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

package com.vwo.logger;

import com.vwo.VWO;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.LoggerFactory;


public class Logger {
  private org.slf4j.Logger logger;
  private static VWOLogger customLogger;
  private static ArrayList<String> logPriority = new ArrayList<>(Arrays.asList(
      VWO.Enums.LOGGER_LEVEL.TRACE.value(),
      VWO.Enums.LOGGER_LEVEL.DEBUG.value(),
      VWO.Enums.LOGGER_LEVEL.INFO.value(),
      VWO.Enums.LOGGER_LEVEL.WARN.value(),
      VWO.Enums.LOGGER_LEVEL.ERROR.value()
  ));

  private Logger(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  private static boolean shouldAllowLogLevel(String level) {
    if (Logger.customLogger != null) {
      return logPriority.indexOf(Logger.customLogger.level) <= logPriority.indexOf(level);
    }

    return true;
  }

  public static void init(VWOLogger customLogger) {
    if (customLogger != null && logPriority.indexOf(customLogger.level) == -1) {
      customLogger.level = VWO.Enums.LOGGER_LEVEL.ERROR.value();
    }
    Logger.customLogger = customLogger;
  }

  public static Logger getLogger(Class<?> clazz) {
    return new Logger(clazz);
  }

  public void trace(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.TRACE.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.trace(var1);
      } else {
        this.logger.trace(var1);
      }
    }
  }

  public void trace(String var1, boolean disableLogs) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.TRACE.value()) && disableLogs) {
      if (Logger.customLogger != null) {
        Logger.customLogger.trace(var1);
      } else {
        this.logger.trace(var1);
      }
    }
  }

  public void trace(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.TRACE.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.trace(var1, var2);
      } else {
        this.logger.trace(var1, var2);
      }
    }
  }

  public void debug(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.DEBUG.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.debug(var1);
      } else {
        this.logger.debug(var1);
      }
    }
  }

  public void debug(String var1, boolean disableLogs) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.DEBUG.value()) && !disableLogs) {
      if (Logger.customLogger != null) {
        Logger.customLogger.debug(var1);
      } else {
        this.logger.debug(var1);
      }
    }
  }


  public void debug(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.DEBUG.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.debug(var1, var2);
      } else {
        this.logger.debug(var1, var2);
      }
    }
  }

  public void info(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.INFO.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.info(var1);
      } else {
        this.logger.info(var1);
      }
    }
  }

  public void info(String var1, boolean disableLogs) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.INFO.value()) && !disableLogs) {
      if (Logger.customLogger != null) {
        Logger.customLogger.info(var1);
      } else {
        this.logger.info(var1);
      }
    }
  }

  public void info(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.INFO.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.info(var1, var2);
      } else {
        this.logger.info(var1, var2);
      }
    }
  }

  public void warn(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.WARN.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.warn(var1);
      } else {
        this.logger.warn(var1);
      }
    }
  }

  public void warn(String var1, boolean disableLogs) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.WARN.value()) && !disableLogs) {
      if (Logger.customLogger != null) {
        Logger.customLogger.warn(var1);
      } else {
        this.logger.warn(var1);
      }
    }
  }

  public void warn(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.WARN.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.warn(var1, var2);
      } else {
        this.logger.warn(var1, var2);
      }
    }
  }

  public void error(String var1) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.ERROR.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.error(var1);
      } else {
        this.logger.error(var1);
      }
    }
  }

  public void error(String var1, boolean disableLogs) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.ERROR.value()) && !disableLogs) {
      if (Logger.customLogger != null) {
        Logger.customLogger.error(var1);
      } else {
        this.logger.error(var1);
      }
    }
  }

  public void error(String var1, Object... var2) {
    if (shouldAllowLogLevel(VWO.Enums.LOGGER_LEVEL.ERROR.value())) {
      if (Logger.customLogger != null) {
        Logger.customLogger.error(var1, var2);
      } else {
        this.logger.error(var1, var2);
      }
    }
  }
}