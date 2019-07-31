package com.vwo;

import com.vwo.config.ConfigParseException;
import com.vwo.models.SettingFileConfig;

public interface Parser {
    SettingFileConfig isValid(String settingFile) throws ConfigParseException;


}
