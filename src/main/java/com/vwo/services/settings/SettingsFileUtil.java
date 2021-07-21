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

package com.vwo.services.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.models.Settings;
import com.vwo.enums.LoggerMessagesEnums;
import com.vwo.logger.Logger;
import com.vwo.models.Campaign;
import com.vwo.models.Variation;

import java.util.HashMap;
import java.util.List;

public class SettingsFileUtil implements SettingFile {

  private Settings settings;
  private static final double MAX_TRAFFIC_VALUE = 10000;
  private static final Logger LOGGER = Logger.getLogger(SettingsFileUtil.class);

  public static SettingFile initializeSettingsFile(String settingFileString) {
    if (settingFileString != null && !settingFileString.isEmpty()) {
      try {
        SettingFile settingFile = SettingsFileUtil.Builder.getInstance(settingFileString).build();
        settingFile.processSettingsFile();
        LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.SETTINGS_FILE_PROCESSED.value());

        return settingFile;
      } catch (Exception e) {
        LOGGER.error(LoggerMessagesEnums.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
      }
    }

    return null;
  }

  public static void setVariationRange(List<Variation> variations) {
    double allocatedRange = 0;

    for (Variation variation : variations) {
      Double stepFactor = getVariationBucketRange(variation.getWeight());
      if (stepFactor != null && stepFactor != -1) {
        variation.setStartRangeVariation((int) allocatedRange + 1);
        allocatedRange = (Math.ceil(allocatedRange + stepFactor));
        variation.setEndRangeVariation((int) allocatedRange);
      } else {
        variation.setStartRangeVariation(-1);
        variation.setEndRangeVariation(-1);
      }

      LOGGER.info(LoggerMessagesEnums.DEBUG_MESSAGES.VARIATION_RANGE_ALLOCATED.value(new HashMap<String, String>() {
        {
          put("variation", variation.getName());
          put("weight", String.valueOf(variation.getWeight()));
          put("startRange", String.valueOf(variation.getStartRangeVariation()));
          put("endRange", String.valueOf(variation.getEndRangeVariation()));
        }
      }));
    }
  }


  public static Double getVariationBucketRange(double variationWeight) {
    double startRange = variationWeight * 100;
    if (startRange == 0) {
      startRange = -1;
    }
    return Math.min(startRange, MAX_TRAFFIC_VALUE);
  }

  public SettingsFileUtil(Settings settings) {
    this.settings = settings;
  }

  public Settings processSettingsFile() {
    List<Campaign> campaigns = settings.getCampaigns();
    for (Campaign campaign : campaigns) {
      setVariationRange(campaign.getVariations());
    }
    return this.settings;
  }

  public Campaign getCampaign(String campaignKey) {
    for (Campaign campaign : settings.getCampaigns()) {
      if (campaign.getKey().equalsIgnoreCase(campaignKey)) {
        LOGGER.debug(LoggerMessagesEnums.DEBUG_MESSAGES.CAMPAIGN_KEY_FOUND.value(new HashMap<String, String>() {
          {
            put("campaignKey", campaignKey);
          }
        }));
        return campaign;
      }
    }
    return null;
  }

  public Settings getSettings() {
    return settings;
  }

  public static class Builder {

    private String settingFile;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Parser parser = null;

    private Builder(String settingFile) {
      this.settingFile = settingFile;
    }

    public static Builder getInstance(String settingFile) {
      Builder builder = new Builder(settingFile);
      if (builder.parser == null) {
        builder.parser = new SettingsParser(builder.objectMapper);
      }
      return builder;
    }

    public Builder withParser(Parser parser) {
      this.parser = parser;
      return this;
    }

    public SettingsFileUtil build() throws Exception {
      Settings settings = parser.parseSettingsFile(settingFile);
      return new SettingsFileUtil(settings);
    }
  }
}
