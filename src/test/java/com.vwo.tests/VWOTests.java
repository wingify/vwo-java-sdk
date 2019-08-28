package com.vwo.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vwo.VWO;
import com.vwo.event.EventHandler;
import com.vwo.logger.LoggerManager;
import com.vwo.logger.VWOLogger;
import com.vwo.tests.data.Settings;
import com.vwo.userprofile.UserProfileService;
import org.junit.jupiter.api.Test;


public class VWOTests {
  private static final LoggerManager LOGGER = LoggerManager.getLogger(VWOTests.class);


  @Test
  public void hasRequiredModules() throws NoSuchMethodException {
    LOGGER.info("Should export required modules and APIs");

    Class[] createInstanceArg = new Class[1];
    createInstanceArg[0] = String.class;

    Class[] getSettingsArg = new Class[2];
    getSettingsArg[0] = String.class;
    getSettingsArg[1] = String.class;

    assertTrue(VWO.class.getMethod("createInstance", createInstanceArg) != null);
    assertTrue(VWO.class.getMethod("getSettingsFile", getSettingsArg) != null);

    Class vwoClass = VWO.createInstance("").getClass();

    Class[] userProfileArg = new Class[1];
    userProfileArg[0] = UserProfileService.class;
    assertTrue(vwoClass.getMethod("withUserProfileService", userProfileArg) != null);

    Class[] eventHandlerArg = new Class[1];
    eventHandlerArg[0] = EventHandler.class;
    assertTrue(vwoClass.getMethod("withEventHandler", eventHandlerArg) != null);

    Class[] devModeArg = new Class[1];
    devModeArg[0] = boolean.class;
    assertTrue(vwoClass.getMethod("withDevelopmentMode", devModeArg) != null);

    Class[] customLoggerArg = new Class[1];
    customLoggerArg[0] = VWOLogger.class;
    assertTrue(vwoClass.getMethod("withCustomLogger", customLoggerArg) != null);
    assertTrue(vwoClass.getMethod("build", null) != null);

    Class vwoInstanceClass = VWO.createInstance("").build().getClass();

    Class[] variationArg = new Class[2];
    variationArg[0] = String.class;
    variationArg[1] = String.class;
    assertTrue(vwoInstanceClass.getMethod("activate", variationArg) != null);
    assertTrue(vwoInstanceClass.getMethod("getVariation", variationArg) != null);

    Class[] trackArg = new Class[3];
    trackArg[0] = String.class;
    trackArg[1] = String.class;
    trackArg[2] = String.class;
    assertTrue(vwoInstanceClass.getMethod("track", trackArg) != null);
  }

  @Test
  public void settingFileTests() {
    LOGGER.info("Should not process settingsFile if settingsFile is not provided or corrupted");

    VWO vwoInstance = VWO.createInstance("").build();
    assertTrue(vwoInstance.getProjectConfig() == null);

    LOGGER.info("Should process settingsFile if it is provided and is valid");

    vwoInstance = VWO.createInstance(Settings.settings1).build();
    assertTrue(vwoInstance.getProjectConfig() != null);
  }
}