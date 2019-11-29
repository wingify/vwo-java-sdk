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

package com.vwo;

import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.services.core.BucketingService;
import com.vwo.services.core.VariationDecider;
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

import java.util.Map;

/**
 * This file VWO.java has references from "Optimizely Java SDK, version 3.2.0", Copyright 2017-2019, Optimizely,
 * under Apache 2.0 License.
 * Source - https://github.com/optimizely/java-sdk/blob/master/core-api/src/main/java/com/optimizely/ab/Optimizely.java
 */

/**
 * Main VWO class exposed to customers. It needs to be instantiated to access all the APIs.
 */
public class VWO {

  final Storage.User userStorage;
  final SettingFile settingFile;
  final VWOLogger customLogger;
  final VariationDecider variationDecider;
  private boolean developmentMode;

  public static final class Enums extends VWOEnums {}

  private static final Logger LOGGER = Logger.getLogger(VWO.class);

  private VWO(SettingFile settingFile,
              Storage.User userStorage,
              VariationDecider variationDecider,
              VWOLogger customLogger,
              boolean developmentMode) {

    this.userStorage = userStorage;
    this.settingFile = settingFile;
    this.customLogger = customLogger;
    this.developmentMode = developmentMode;
    this.variationDecider = variationDecider;
  }

  public SettingFile getSettingFile() {
    return this.settingFile;
  }

  public Storage.User getuserStorage() {
    return this.userStorage;
  }

  public VariationDecider getVariationDecider() {
    return this.variationDecider;
  }

  public VWOLogger getCustomLogger() {
    return this.customLogger;
  }

  public boolean isDevelopmentMode() {
    return this.developmentMode;
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
    return SettingsFileManager.getSettingsFile(accountID, sdkKey);
  }

  /**
   * Get variation and track conversion on VWO server.
   *
   * @param campaignKey Campaign key
   * @param userId      User ID
   * @return            String variation name, or null if the user doesn't qualify to become a part of the campaign.
   */
  public String activate(String campaignKey, String userId) {
    return ActivateCampaign.activate(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), null);
  }

  /**
   * Get variation and track conversion on VWO server for users falling in provided pre-segmentation.
   *
   * @param campaignKey Campaign key
   * @param userId      User ID
   * @param CustomVariables Pre-segmentation data
   * @return            String variation name, or null if the user doesn't qualify to become a part of the campaign.
   */
  public String activate(String campaignKey, String userId, Map<String, ?> CustomVariables) {
    return ActivateCampaign.activate(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), CustomVariables);
  }

  /**
   * Get variation name.
   *
   * @param campaignKey  Campaign key
   * @param userId       User ID
   * @return             Variation name
   */
  public String getVariationName(String campaignKey, String userId) {
    return CampaignVariation.getVariationName(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), null);
  }

  /**
   * Get variation name for users falling in provided pre-segmentation.
   *
   * @param campaignKey  Campaign key
   * @param userId       User ID
   * @param CustomVariables Pre-segmentation data
   * @return             Variation name
   */
  public String getVariationName(String campaignKey, String userId, Map<String, ?> CustomVariables) {
    return CampaignVariation.getVariationName(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), CustomVariables);
  }

  /**
   * Get variation, tracks conversion event and send to VWO server.
   *
   * @param campaignKey     Campaign key
   * @param userId          User ID
   * @param goalIdentifier  Goal key
   * @param revenueValue    revenue value generated on triggering the goal
   * @return                Boolean value whether user is tracked or not.
   */
  public boolean track(String campaignKey, String userId, String goalIdentifier, Object revenueValue) {
    return TrackCampaign.trackGoal(campaignKey, userId, goalIdentifier, revenueValue, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), null);
  }

  public boolean track(String campaignKey, String userId, String goalIdentifier) {
    return TrackCampaign.trackGoal(campaignKey, userId, goalIdentifier, null, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), null);
  }

  /**
   * Get variation, tracks conversion event and send to VWO server for users falling in provided pre-segmentation.
   *
   * @param campaignKey     Campaign key
   * @param userId          User ID
   * @param goalIdentifier  Goal key
   * @param revenueValue    revenue value generated on triggering the goal
   * @param CustomVariables Pre-segmentation data
   * @return                Boolean value whether user is tracked or not.
   */
  public boolean track(String campaignKey, String userId, String goalIdentifier, Object revenueValue, Map<String, ?> CustomVariables) {
    return TrackCampaign.trackGoal(campaignKey, userId, goalIdentifier, revenueValue, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), CustomVariables);
  }

  public boolean track(String campaignKey, String userId, String goalIdentifier, Map<String, ?> CustomVariables) {
    return TrackCampaign.trackGoal(campaignKey, userId, goalIdentifier, null, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), CustomVariables);
  }

  /**
   * Identifies whether the user became part of feature rollout/test or not.
   *
   * @param campaignKey Unique campaign test key
   * @param userId ID assigned to a user
   * @return Boolean corresponding to whether user became part of feature.
   */
  public boolean isFeatureEnabled(String campaignKey, String userId) {
    return FeatureCampaign.isFeatureEnabled(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), null);
  }

  /**
   * Identifies whether the user became part of feature rollout/test or not for users falling in provided pre-segmentation.
   *
   * @param campaignKey Unique campaign test key
   * @param userId ID assigned to a user
   * @param CustomVariables Pre-segmentation data
   * @return Boolean corresponding to whether user became part of feature.
   */
  public boolean isFeatureEnabled(String campaignKey, String userId, Map<String, ?> CustomVariables) {
    return FeatureCampaign.isFeatureEnabled(campaignKey, userId, this.getSettingFile(), this.getVariationDecider(), this.isDevelopmentMode(), CustomVariables);
  }

  /**
   * Gets the feature variable corresponding to the variable_key passed.
   *
   * @param campaignKey Unique campaign test key
   * @param userId ID assigned to a user
   * @param variableKey Variable name/key
   * @return If variation is assigned then string variable corresponding to variation assigned otherwise null
   */
  public Object getFeatureVariableValue(String campaignKey, String variableKey, String userId) {
    return FeatureCampaign.getFeatureVariable(campaignKey, userId, variableKey, null, this.getSettingFile(), this.getVariationDecider(), null);
  }

  /**
   * Gets the feature variable corresponding to the variable_key passed for users falling in provided pre-segmentation.
   *
   * @param campaignKey Unique campaign test key
   * @param variableKey Variable name/key
   * @param userId ID assigned to a user
   * @param CustomVariables Pre-segmentation data
   * @return If variation is assigned then string variable corresponding to variation assigned otherwise null
   */
  public Object getFeatureVariableValue(String campaignKey, String variableKey, String userId, Map<String, ?> CustomVariables) {
    return FeatureCampaign.getFeatureVariable(campaignKey, userId, variableKey, null, this.getSettingFile(), this.getVariationDecider(), CustomVariables);
  }

  /**
   * Pushes the tag key and value for a user to be used in post segmentation.
   *
   * @param tagKey Tag name
   * @param tagValue Tag value
   * @param userId ID assigned to a user
   * @return Boolean representing if the tag was pushed or not
   */
  public boolean push(String tagKey, String tagValue, String userId) {
    return Segmentation.pushCustomDimension(this.getSettingFile(), tagKey, tagValue, userId, this.isDevelopmentMode());
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
      LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e.getStackTrace());
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
     * Custom user storage instance.
     *
     * @param userStorage Storage.User class instance
     * @return Builder instance
     */
    public Builder withUserStorage(Storage.User userStorage) {
      this.userStorage = userStorage;
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

    private void initializeDefaults() {
      if (this.settingFile == null && this.settingFileString != null && !this.settingFileString.isEmpty()) {
        try {
          this.settingFile = SettingsFileUtil.Builder.getInstance(this.settingFileString).build();
          this.settingFile.processSettingsFile();
          LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SETTINGS_FILE_PROCESSED.value());
        } catch (Exception e) {
          LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
        }
      }

      this.bucketingService = new BucketingService();
      this.variationDecider = new VariationDecider(bucketingService, userStorage);
      this.developmentMode = this.developmentMode || false;
    }

    private VWO createVWOInstance() {
      VWO vwoInstance = new VWO(this.settingFile, this.userStorage, this.variationDecider, this.customLogger, this.developmentMode);
      if (vwoInstance != null) {
        LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SDK_INITIALIZED.value());
      }
      return vwoInstance;
    }
  }
}
