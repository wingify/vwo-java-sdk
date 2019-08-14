package com.vwo.config;

import com.vwo.models.Campaign;
import com.vwo.models.SettingFileConfig;

public interface ProjectConfig {

  SettingFileConfig processSettingsFile();

  Campaign getCampaignTestKey(String campaignTestKey);

  SettingFileConfig getSettingFileConfig();
}
