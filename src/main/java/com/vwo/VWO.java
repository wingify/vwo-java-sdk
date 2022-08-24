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

package com.vwo;

import com.vwo.enums.APIEnums;
import com.vwo.enums.GoalEnums;
import com.vwo.logger.LoggerService;
import com.vwo.models.response.BatchEventData;
import com.vwo.models.response.Settings;
import com.vwo.services.batch.BatchEventQueue;
import com.vwo.services.core.BucketingService;
import com.vwo.services.core.VariationDecider;
import com.vwo.services.datalocation.DataLocationManager;
import com.vwo.services.integrations.HooksManager;
import com.vwo.services.integrations.IntegrationEventListener;
import com.vwo.services.settings.SettingFile;
import com.vwo.services.settings.SettingsFileUtil;
import com.vwo.services.settings.SettingsFileManager;
import com.vwo.services.api.ActivateCampaign;
import com.vwo.services.api.CampaignVariation;
import com.vwo.services.api.FeatureCampaign;
import com.vwo.services.api.Segmentation;
import com.vwo.services.api.TrackCampaign;
import com.vwo.enums.VWOEnums;
import com.vwo.logger.Logger;
import com.vwo.logger.VWOLogger;
import com.vwo.services.storage.Storage;

import java.util.HashMap;
import java.util.Map;

/**
 * Main VWO class exposed to customers. It needs to be instantiated to access all the APIs.
 */
public class VWO {

  Storage.User userStorage;
  VWOLogger customLogger;
  VariationDecider variationDecider;
  GoalEnums.GOAL_TYPES goalTypeToTrack;
  Integer pollingInterval;
  String sdkKey;
  private String settingFileString;
  private SettingFile settingFile;
  private BatchEventQueue batchEventQueue;
  private boolean developmentMode;
  private Map<String, Integer> usageStats;
  private boolean optOut = false;

  public static final class Enums extends VWOEnums {
  }

  public static final class AdditionalParams extends VWOAdditionalParams {
  }

  private static final Logger LOGGER = Logger.getLogger(VWO.class);

  private VWO(
          SettingFile settingFile,
          String settingFileString,
          Storage.User userStorage,
          VariationDecider variationDecider,
          VWOLogger customLogger,
          boolean developmentMode,
          GoalEnums.GOAL_TYPES goalTypeToTrack,
          Integer pollingInterval,
          String sdkKey,
          BatchEventQueue batchEventQueue,
          Map<String, Integer> usageStats
  ) {
    this.userStorage = userStorage;
    this.settingFile = settingFile;
    this.settingFileString = settingFileString;
    this.customLogger = customLogger;
    this.developmentMode = developmentMode;
    this.variationDecider = variationDecider;
    this.goalTypeToTrack = goalTypeToTrack;
    this.pollingInterval = pollingInterval;
    this.sdkKey = sdkKey;
    this.batchEventQueue = batchEventQueue;
    this.usageStats = usageStats;

    if (this.pollingInterval != null && this.sdkKey != null) {
      this.pollSettingsFile();
    }
  }

  /**
   * Poll settings file after every pollingInterval ms by spawning a new thread.
   */
  private void pollSettingsFile() {
    new Thread(() -> {
      while (!optOut) {
        try {
          fetchAndUpdateSettings();
          Thread.sleep(this.pollingInterval);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * Fetch settings file and update it on the instance in case settings differ from the previous fetched settings.
   */
  private void fetchAndUpdateSettings() {
    Settings settings = this.settingFile.getSettings();
    String accountId = settings.getAccountId().toString();
    Map<String, String> loggingParams = new HashMap<String, String>() {
      {
        put("accountID", accountId);
      }
    };

    try {
      String settingsFileString = this.getSettingsFile(accountId, this.sdkKey);

      if (!settingsFileString.equals(this.settingFileString)) {
        LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("POLLING_SETTINGS_FILE_UPDATED"), new HashMap<String, String>() {
          {
            put("accountId", String.valueOf(accountId));
          }
        }));
        updateSettingsFile(settingsFileString);
      } else {
        LOGGER.info(LoggerService.getInstance().infoMessages.get("POLLING_SETTINGS_FILE_NOT_UPDATED"));
      }

      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("POLLING_SUCCESS"), new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
        }
      }));
    } catch (Exception e) {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("POLLING_FAILED"), new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
        }
      }));
    }
  }

  /**
   * Update the settings-file on the instance so that latest settings could be used from next hit onwards.
   *
   * @param settingsFileString Stringified settings file.
   */
  private void updateSettingsFile(String settingsFileString) {
    this.settingFileString = settingsFileString;
    this.settingFile = SettingsFileUtil.initializeSettingsFile(settingsFileString);
    DataLocationManager.getInstance().setSettings(this.settingFile.getSettings());
  }

  public SettingFile getSettingFile() {
    return this.settingFile;
  }

  public String getSettingFileString() {
    return this.settingFileString;
  }

  public Storage.User getUserStorage() {
    return this.userStorage;
  }

  public VariationDecider getVariationDecider() {
    return this.variationDecider;
  }

  public VWOLogger getCustomLogger() {
    return this.customLogger;
  }

  public BatchEventQueue getBatchEventQueue() {
    return this.batchEventQueue;
  }

  public boolean isDevelopmentMode() {
    return this.developmentMode;
  }

  public Map<String, Integer> getUsageStats() {
    return this.usageStats;
  }

  public void setDevelopmentMode(boolean developmentMode) {
    this.developmentMode = developmentMode;
  }

  /**
   * Fetch the account settings file.
   *
   * @param accountID VWO application account-id.
   * @param sdkKey    Unique sdk-key
   * @return Campaign settings
   */
  public static String getSettingsFile(String accountID, String sdkKey) {
    return SettingsFileManager.getSettingsFile(accountID, sdkKey, false);
  }

  /**
   * Get variation and track conversion on VWO server for users falling in provided pre-segmentation.
   *
   * @param campaignKey      Campaign key
   * @param userId           User ID
   * @param additionalParams Any Additional params (customVariables, variationTargetingVariables)
   * @return String variation name, or null if the user doesn't qualify to become a part of the campaign.
   */
  public String activate(String campaignKey, String userId, VWOAdditionalParams additionalParams) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.ACTIVATE.value());
        }
      }));
      return null;
    }
    additionalParams = additionalParams == null ? new VWO.AdditionalParams() : additionalParams;
    return ActivateCampaign.activate(
            campaignKey,
            userId,
            this.getSettingFile(),
            this.getVariationDecider(),
            this.isDevelopmentMode(),
            this.batchEventQueue,
            this.usageStats,
            additionalParams.getCustomVariables(),
            additionalParams.getVariationTargetingVariables()
    );
  }

  /**
   * Get variation and track conversion on VWO server.
   *
   * @param campaignKey Campaign key
   * @param userId      User ID
   * @return String variation name, or null if the user doesn't qualify to become a part of the campaign.
   */
  public String activate(String campaignKey, String userId) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.ACTIVATE.value());
        }
      }));
      return null;
    }
    return ActivateCampaign.activate(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), this.batchEventQueue, this.usageStats, null, null);
  }

  /**
   * Get variation name for users falling in provided pre-segmentation.
   *
   * @param campaignKey      Campaign key
   * @param userId           User ID
   * @param additionalParams Any Additional params (customVariables, variationTargetingVariables)
   * @return Variation name
   */
  public String getVariationName(String campaignKey, String userId, VWOAdditionalParams additionalParams) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.GET_VARIATION_NAME.value());
        }
      }));
      return null;
    }
    additionalParams = additionalParams == null ? new VWO.AdditionalParams() : additionalParams;

    return CampaignVariation.getVariationName(
            campaignKey,
            userId,
            this.getSettingFile(),
            this.getVariationDecider(),
            additionalParams.getCustomVariables(),
            additionalParams.getVariationTargetingVariables()
    );
  }

  /**
   * Get variation name.
   *
   * @param campaignKey  Campaign key
   * @param userId       User ID
   * @return Variation name
   */
  public String getVariationName(String campaignKey, String userId) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.GET_VARIATION_NAME.value());
        }
      }));
      return null;
    }
    return CampaignVariation.getVariationName(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), null, null);
  }

  /**
   * Get variation, tracks conversion event and send to VWO server.
   *
   * @param campaignKey      Campaign key or array of Strings containing campaign keys or null
   * @param userId           User ID
   * @param goalIdentifier   Goal key
   * @param additionalParams Any Additional params (revenueValue, customVariables, variationTargetingVariables)
   * @return Map containing the campaign name and their boolean status representing if tracked or not, and null if something went wrong.
   */
  public Map<String, Boolean> track(Object campaignKey, String userId, String goalIdentifier, VWOAdditionalParams additionalParams) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.TRACK.value());
        }
      }));
      return null;
    }
    additionalParams = additionalParams == null ? new VWO.AdditionalParams() : additionalParams;
    GoalEnums.GOAL_TYPES goalsToTrack = additionalParams.getGoalTypeToTrack() == null
            ? this.goalTypeToTrack
            : additionalParams.getGoalTypeToTrack();

    return TrackCampaign.trackGoal(
            campaignKey,
            userId,
            goalIdentifier,
            additionalParams.getRevenueValue(),
            this.getSettingFile(),
            this.getVariationDecider(),
            this.isDevelopmentMode(),
            this.batchEventQueue,
            additionalParams.getCustomVariables(),
            additionalParams.getVariationTargetingVariables(),
            goalsToTrack,
            this.usageStats,
            additionalParams.getEventProperties()
    );
  }

  public Map<String, Boolean> track(Object campaignKey, String userId, String goalIdentifier) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.TRACK.value());
        }
      }));
      return null;
    }
    return TrackCampaign.trackGoal(
            campaignKey,
            userId,
            goalIdentifier,
            null,
            this.getSettingFile(),
            this.getVariationDecider(),
            this.isDevelopmentMode(),
            this.batchEventQueue,
            null,
            null,
            this.goalTypeToTrack,
            this.usageStats,
            new HashMap<>()
    );
  }

  /**
   * Identifies whether the user became part of feature rollout/test or not.
   *
   * @param campaignKey      Unique campaign test key
   * @param userId           ID assigned to a user
   * @param additionalParams Any Additional params (customVariables, variationTargetingVariables)
   * @return Boolean corresponding to whether user became part of feature.
   */
  public boolean isFeatureEnabled(String campaignKey, String userId, VWOAdditionalParams additionalParams) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.IS_FEATURE_ENABLED.value());
        }
      }));
      return false;
    }

    additionalParams = additionalParams == null ? new VWO.AdditionalParams() : additionalParams;

    return FeatureCampaign.isFeatureEnabled(
            campaignKey,
            userId,
            this.getSettingFile(),
            this.getVariationDecider(),
            this.isDevelopmentMode(),
            this.batchEventQueue,
            this.usageStats,
            additionalParams.getCustomVariables(),
            additionalParams.getVariationTargetingVariables()
    );
  }

  /**
   * Identifies whether the user became part of feature rollout/test or not for users falling in provided pre-segmentation.
   *
   * @param campaignKey Unique campaign test key
   * @param userId      ID assigned to a user
   * @return Boolean corresponding to whether user became part of feature.
   */
  public boolean isFeatureEnabled(String campaignKey, String userId) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.IS_FEATURE_ENABLED.value());
        }
      }));
      return false;
    }
    return FeatureCampaign.isFeatureEnabled(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), this.batchEventQueue, this.usageStats, null, null);
  }

  /**
   * Gets the feature variable corresponding to the variable_key passed.
   *
   * @param campaignKey      Unique campaign test key
   * @param userId           ID assigned to a user
   * @param variableKey      Variable name/key
   * @param additionalParams Any Additional params (customVariables, variationTargetingVariables)
   * @return If variation is assigned then string variable corresponding to variation assigned otherwise null
   */
  public Object getFeatureVariableValue(String campaignKey, String variableKey, String userId, VWOAdditionalParams additionalParams) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.GET_FEATURE_VARIABLE_VALUE.value());
        }
      }));
      return null;
    }

    additionalParams = additionalParams == null ? new VWO.AdditionalParams() : additionalParams;

    return FeatureCampaign.getFeatureVariable(
            campaignKey,
            userId,
            variableKey,
            null,
            this.getSettingFile(),
            this.getVariationDecider(),
            additionalParams.getCustomVariables(),
            additionalParams.getVariationTargetingVariables()
    );
  }

  /**
   * Gets the feature variable corresponding to the variable_key passed for users falling in provided pre-segmentation.
   *
   * @param campaignKey Unique campaign test key
   * @param variableKey Variable name/key
   * @param userId      ID assigned to a user
   * @return If variation is assigned then string variable corresponding to variation assigned otherwise null
   */
  public Object getFeatureVariableValue(String campaignKey, String variableKey, String userId) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.GET_FEATURE_VARIABLE_VALUE.value());
        }
      }));
      return null;
    }

    return FeatureCampaign.getFeatureVariable(campaignKey, userId, variableKey, null, this.getSettingFile(), this.getVariationDecider(), null, null);
  }

  /**
   * Pushes the tag key and value for a user to be used in post segmentation.
   *
   * @param tagKey   Tag name
   * @param tagValue Tag value
   * @param userId   ID assigned to a user
   * @return Boolean representing if the tag was pushed or not
   */
  public Map<String, Boolean> push(String tagKey, String tagValue, String userId) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.PUSH.value());
        }
      }));
      return new HashMap<String, Boolean>();
    }

    return Segmentation.pushCustomDimension(this.getSettingFile(), tagKey, tagValue, userId, this.batchEventQueue, this.isDevelopmentMode(), new HashMap<>(), this.usageStats);
  }

  /**
   * Pushes the tag key and value for a user to be used in post segmentation.
   *
   * @param userId   ID assigned to a user
   * @return Boolean representing if the tag was pushed or not
   */
  public Map<String, Boolean> push(Map<String, String> customDimensionMap,  String userId) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.PUSH.value());
        }
      }));
      return new HashMap<String, Boolean>();
    }

    return Segmentation.pushCustomDimension(this.getSettingFile(), " ", " ", userId, this.batchEventQueue, this.isDevelopmentMode(), customDimensionMap, this.usageStats);
  }

  /**
   * Manually flush impression events to VWO which are queued in batch queue as per batchEvents config.
   *
   * @return Boolean representing if the events are flushed or not
   */
  public boolean flushEvents() {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.FLUSH_EVENTS.value());
        }
      }));
      return false;
    }

    int accountId = this.settingFile.getSettings().getAccountId();
    if (this.batchEventQueue != null) {

      LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("EVENT_BATCH_FLUSH"), new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
          put("queueLength", String.valueOf(batchEventQueue.getBatchQueue().size()));
        }
      }));
      return this.batchEventQueue.flushAndClearInterval();
    } else {
      LOGGER.error(LoggerService.getComputedMsg(LoggerService.getInstance().errorMessages.get("BATCH_QUEUE_EMPTY"), new HashMap<String, String>() {
        {
          put("accountId", String.valueOf(accountId));
        }
      }));
      return false;
    }
  }

  /**
   * Fetch latest settings-file and update so that vwoClientInstance could use latest settings.
   * Helpful especially when using webhooks
   *
   * @param accountId VWO application account-id.
   * @param sdkKey    Unique sdk-key
   */
  public void getAndUpdateSettingsFile(String accountId, String sdkKey) {
    if (this.optOut) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("API_NOT_ENABLED"), new HashMap<String, String>() {
        {
          put("api", APIEnums.API_TYPES.UPDATE_SETTINGS_FILE.value());
        }
      }));
      return;
    }

    String updatedSettings = SettingsFileManager.getSettingsFile(accountId, sdkKey, true);
    if (updatedSettings != null) {
      this.updateSettingsFile(updatedSettings);
    }
  }

  /**
   * Manually opting out of VWO SDK, No tracking will happen.
   *
   * @return boolean
   */
  public boolean setOptOut() {
    LOGGER.info(LoggerService.getInstance().infoMessages.get("OPT_OUT_API_CALLED"));
    if (this.batchEventQueue != null && !this.batchEventQueue.getBatchQueue().isEmpty()) {
      this.flushEvents();
    }
    this.destroyInstanceVariables();
    return true;
  }


  private void destroyInstanceVariables() {
    this.settingFile = null;
    this.customLogger = null;
    this.userStorage = null;
    this.batchEventQueue = null;
    this.settingFileString = null;
    this.pollingInterval = 0;
    this.variationDecider = null;
    this.goalTypeToTrack = null;
    this.usageStats = null;
    this.sdkKey = null;
    this.batchEventQueue = null;
    this.optOut = true;
  }

  //  /**
  //   * Gets the feature details for a campaign.
  //   *
  //   * @param campaignKey Unique campaign test key
  //   * @return If variation is assigned then string variable corresponding to variation assigned otherwise null
  //   */
  //  public List<Variable> getFeatureDetails(String campaignKey) {
  //    return FeatureCampaign.getFeatureDetails(campaignKey, this.getSettingFile());
  //  }

  /**
   * Creates builder instance.`
   *
   * @param settingFile - Setting string
   * @return - Builder instance
   */
  public static Builder launch(String settingFile) {
    try {
      return new Builder().withSettingFile(settingFile);
    } catch (Exception e) {
      //LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e.getStackTrace());
    }
    return null;
  }

  public static class Builder {
    private SettingFile settingFile;
    private Storage.User userStorage;
    private VariationDecider variationDecider;
    private String settingFileString;
    private BucketingService bucketingService;
    private VWOLogger customLogger;
    private boolean developmentMode;
    private GoalEnums.GOAL_TYPES goalTypeToTrack = GoalEnums.GOAL_TYPES.ALL;
    private Integer pollingInterval;
    private String sdkKey;
    private BatchEventData batchEvents;
    private BatchEventQueue batchEventsQueue;
    private IntegrationEventListener integrations;
    private Map<String, Integer> usageStats = new HashMap<String, Integer>(){};


    /**
     * Pass the campaign settings file.
     *
     * @param settingFileString JSON stringified settings.
     * @return Builder instance
     */
    private Builder withSettingFile(String settingFileString) {
      this.settingFileString = settingFileString;
      return this;
    }

    /**
     * Add Polling Interval.
     *
     * @param pollingInterval Polling interval in ms
     * @return Builder instance
     */
    public Builder withPollingInterval(Integer pollingInterval) {
      this.pollingInterval = pollingInterval;
      usageStats.put("pi", 1);
      logInfo("pollingInterval", "number(in milliseconds)");
      return this;
    }

    /**
     * Add SDK key for polling.
     *
     * @param sdkKey SDK key used to fetch settings file incase of polling.
     * @return Builder instance
     */
    public Builder withSdkKey(String sdkKey) {
      this.sdkKey = sdkKey;
      return this;
    }

    /**
     * Custom user storage instance.
     *
     * @param userStorage Storage.User class instance
     * @return Builder instance
     */
    public Builder withUserStorage(Storage.User userStorage) {
      this.userStorage = userStorage;
      usageStats.put("ss", 1);
      logInfo("userStorageService", "object");
      return this;
    }

    /**
     * Toggle development mode.
     *
     * @param developmentMode Boolean value specifying development mode is on ir off
     * @return Builder instance
     */
    public Builder withDevelopmentMode(boolean developmentMode) {
      this.developmentMode = developmentMode;
      logInfo("developmentMode", "boolean");
      return this;
    }

    /**
     * Custom logger.
     *
     * @param customLogger VWOLogger instance
     * @return Builder instance
     */
    public Builder withCustomLogger(VWOLogger customLogger) {
      this.customLogger = customLogger;
      usageStats.put("cl", 1);
      LOGGER.debug(LoggerService.getInstance().debugMessages.get("CONFIG_CUSTOM_LOGGER_USED"));
      if (!VWOLogger.level.equals(VWOEnums.LOGGER_LEVEL.ERROR.value())) {
        LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("CONFIG_LOG_LEVEL_SET"), new HashMap<String, String>() {
          {
            put("level", VWOLogger.level);
          }
        }));
        usageStats.put("ll", 1);
      }

      return this;
    }

    /**
     * Notify whether the goal should be tracked again if its already being tracked.
     *
     * @param value true if goal should be tracked every time.
     * @return Builder instance
     */
    public Builder withGoalTypeToTrack(GoalEnums.GOAL_TYPES value) {
      this.goalTypeToTrack = value;
      usageStats.put("gt", 1);
      logInfo("goalTypeToTrack", "GoalEnum(ALL, REVENUE, CUSTOM)");
      return this;
    }

    public Builder withBatchEvents(BatchEventData batchEvents) {
      this.batchEvents = batchEvents;
      usageStats.put("eb", 1);
      logInfo("batchEvents", "Object(BatchEventData)");
      return this;
    }

    public Builder withIntegrations(IntegrationEventListener integrations) {
      this.integrations = integrations;
      usageStats.put("ig", 1);
      logInfo("integrations", "Object(IntegrationEventListener)");
      return this;
    }

    /**
     * Creates a new VWO instance.
     *
     * @return VWO instance
     */
    public VWO build() {
      // Init logger at start.
      Logger.init(this.customLogger);

      this.initializeDefaults();
      return this.createVWOInstance();
    }

    private void logInfo(String parameter, String type) {
      LOGGER.info(LoggerService.getComputedMsg(LoggerService.getInstance().infoMessages.get("CONFIG_PARAMETER_USED"), new HashMap<String, String>() {
        {
          put("parameter", parameter);
          put("type", type);
        }
      }));
    }

    private void initializeDefaults() {
      try {
        LOGGER.info(LoggerService.getInstance().infoMessages.get("CONFIG_VALID"));

        this.settingFile = SettingsFileUtil.initializeSettingsFile(this.settingFileString);

        if (this.pollingInterval != null) {
          if (this.sdkKey != null) {
            LOGGER.debug(LoggerService.getComputedMsg(LoggerService.getInstance().debugMessages.get("POLLING_SETTINGS_FILE_REGISTERED"), new HashMap<String, String>() {
              {
                put("pollingInterval", pollingInterval.toString());
              }
            }));
          } else {
            LOGGER.error(LoggerService.getInstance().errorMessages.get("CONFIG_POLLING_SDK_KEY_NOT_PROVIVED"));
          }
        }

        if (this.settingFile != null) {
          DataLocationManager.getInstance().setSettings(this.settingFile.getSettings());
          this.bucketingService = new BucketingService();
          this.variationDecider = new VariationDecider(bucketingService, userStorage, new HooksManager(this.integrations),
            settingFile.getSettings().getAccountId());
          this.developmentMode = this.developmentMode || false;

          if (developmentMode) {
            LOGGER.debug(LoggerService.getInstance().debugMessages.get("CONFIG_DEVELOPMENT_MODE_STATUS"));
            usageStats.clear();
          }

          if (this.batchEvents != null) {
            batchEventsQueue = new BatchEventQueue(this.batchEvents, settingFile.getSettings().getSdkKey(), settingFile.getSettings().getAccountId(), this.developmentMode, this.usageStats);
          }
        } else {
          LOGGER.error(LoggerService.getInstance().errorMessages.get("SETTINGS_FILE_CORRUPTED"));
        }

      } catch (Exception e) {
        LOGGER.error("Something Went Wrong",e);
      }

    }

    private VWO createVWOInstance() {
      VWO vwoInstance = new VWO(
              this.settingFile,
              this.settingFileString,
              this.userStorage,
              this.variationDecider,
              this.customLogger,
              this.developmentMode,
              this.goalTypeToTrack,
              this.pollingInterval,
              this.sdkKey,
              this.batchEventsQueue,
              this.usageStats
      );
      if (this.settingFile != null &&  vwoInstance != null) {

        LOGGER.info(LoggerService.getInstance().infoMessages.get("SDK_INITIALIZED"));
      }
      return vwoInstance;
    }
  }
}
