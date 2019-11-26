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

package com.vwo.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.VWO;
import com.vwo.logger.Logger;
import com.vwo.logger.VWOLogger;
import com.vwo.models.Settings;
import com.vwo.tests.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class CustomLoggerTests {
  private static final Logger LOGGER = Logger.getLogger(CustomLoggerTests.class);

  @Test
  public void customLoggerMethodsTest() throws IOException {
    LOGGER.info("Should invoke all custom logger methods");

    VWOLogger customLogger = Mockito.spy(this.getCustomLogger(VWO.Enums.LOGGER_LEVEL.TRACE.value()));
    this.launchAndInvokeLogs(customLogger);

    try {
      verify(customLogger, atLeastOnce()).trace(any());
      verify(customLogger, atLeastOnce()).debug(any());
      verify(customLogger, atLeastOnce()).info(any());
      verify(customLogger, atLeastOnce()).warn(any());
      verify(customLogger, atLeastOnce()).error(any());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void customLoggerLevelTest() throws IOException {
    LOGGER.info("Should invoke only custom logger methods which are above mentioned logger level");

    VWOLogger customLogger = Mockito.spy(this.getCustomLogger(VWO.Enums.LOGGER_LEVEL.WARN.value()));
    this.launchAndInvokeLogs(customLogger);

    try {
      verify(customLogger, never()).trace(any());
      verify(customLogger, never()).debug(any());
      verify(customLogger, never()).info(any());
      verify(customLogger, atLeastOnce()).warn(any());
      verify(customLogger, atLeastOnce()).error(any());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void invalidCustomLoggerLevelTest() throws IOException {
    LOGGER.info("Should take error as default log level if incorrect value passed");

    VWOLogger customLogger = Mockito.spy(this.getCustomLogger("INCORRECT"));
    this.launchAndInvokeLogs(customLogger);

    try {
      verify(customLogger, never()).trace(any());
      verify(customLogger, never()).debug(any());
      verify(customLogger, never()).info(any());
      verify(customLogger, never()).warn(any());
      verify(customLogger, atLeastOnce()).error(any());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void defaultCustomLoggerLevelTest() throws IOException {
    LOGGER.info("Should take error as default log level if not passed");

    VWOLogger customLogger = Mockito.spy(this.getCustomLoggerWithoutLevel());
    this.launchAndInvokeLogs(customLogger);

    try {
      verify(customLogger, never()).trace(any());
      verify(customLogger, never()).debug(any());
      verify(customLogger, never()).info(any());
      verify(customLogger, never()).warn(any());
      verify(customLogger, atLeastOnce()).error(any());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void defaultLoggerTest() throws IOException {
    LOGGER.info("Should take error as default log level if incorrect value passed");

    Logger.init(null);
    Logger logger = Mockito.spy(Logger.getLogger(CustomLoggerTests.class));
    this.invokeLogs(logger);

    try {
      verify(logger, atLeastOnce()).trace(any());
      verify(logger, atLeastOnce()).debug(any());
      verify(logger, atLeastOnce()).info(any());
      verify(logger, atLeastOnce()).warn(any());
      verify(logger, atLeastOnce()).error(any());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private VWOLogger getCustomLogger(String level) {
    return new VWOLogger(level) {

      @Override
      public void trace(String message, Object... params) { }

      @Override
      public void debug(String message, Object... params) { }

      @Override
      public void info(String message, Object... params) { }

      @Override
      public void warn(String message, Object... params) { }

      @Override
      public void error(String message, Object... params) { }
    };
  }

  private VWOLogger getCustomLoggerWithoutLevel() {
    return new VWOLogger() {

      @Override
      public void trace(String message, Object... params) { }

      @Override
      public void debug(String message, Object... params) { }

      @Override
      public void info(String message, Object... params) { }

      @Override
      public void warn(String message, Object... params) { }

      @Override
      public void error(String message, Object... params) { }
    };
  }

  private void launchAndInvokeLogs(VWOLogger customLogger) throws IOException {
    Settings settingsConfig = new ObjectMapper().readValue(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50, Settings.class);
    String campaignKey = settingsConfig.getCampaigns().get(0).getKey();

    VWO vwoInstance = VWO.launch(com.vwo.tests.data.Settings.AB_TRAFFIC_50_WEIGHT_50_50).withCustomLogger(customLogger).build();
    vwoInstance.getVariationName(campaignKey, TestUtils.getUsers()[0]);

    this.invokeLogs(LOGGER);
  }

  private void invokeLogs(Logger LOGGER) throws IOException {
    LOGGER.trace("my message");
    LOGGER.trace("my message", "my value");

    LOGGER.debug("my message");
    LOGGER.debug("my message", "my value");

    LOGGER.info("my message");
    LOGGER.info("my message", "my value");

    LOGGER.warn("my message");
    LOGGER.warn("my message", "my value");

    LOGGER.error("my message");
    LOGGER.error("my message", "my value");
  }
}