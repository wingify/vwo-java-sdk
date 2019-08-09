package com.vwo.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerManager {
    private Logger logger;
    private static VWOLogger customLogger;

    private LoggerManager(Class<?> clazz) {
       this.logger = LoggerFactory.getLogger(clazz);
    }

    public static void init(VWOLogger customLogger) {
        LoggerManager.customLogger = customLogger;
    }

    public static LoggerManager getLogger(Class<?> clazz) {
        return new LoggerManager(clazz);
    }

    public void trace(String var1) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.trace(var1);
        } else {
            this.logger.trace(var1);
        }
    }

    public void trace(String var1, Object... var2) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.trace(var1, var2);
        } else {
            this.logger.trace(var1, var2);
        }
    }

    public void debug(String var1) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.debug(var1);
        } else {
            this.logger.debug(var1);
        }
    }


    public void debug(String var1, Object... var2) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.debug(var1, var2);
        } else {
            this.logger.debug(var1, var2);
        }
    }

    public void info(String var1) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.info(var1);
        } else {
            this.logger.info(var1);
        }
    }

    public void info(String var1, Object... var2) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.info(var1, var2);
        } else {
            this.logger.info(var1, var2);
        }
    }

    public void warn(String var1) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.warn(var1);
        } else {
            this.logger.warn(var1);
        }
    }
    public void warn(String var1, Object... var2) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.warn(var1, var2);
        } else {
            this.logger.warn(var1, var2);
        }
    }

    public void error(String var1) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.error(var1);
        } else {
            this.logger.error(var1);
        }
    }

    public void error(String var1, Object... var2) {
        if (LoggerManager.customLogger != null) {
            LoggerManager.customLogger.error(var1, var2);
        } else {
            this.logger.error(var1, var2);
        }
    }
}