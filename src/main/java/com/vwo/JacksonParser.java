package com.vwo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.config.ConfigParseException;
import com.vwo.models.SettingFileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonParser implements Parser{
    private ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonParser.class);

    public JacksonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SettingFileConfig isValid(String settingFile) throws ConfigParseException {
        try {
            SettingFileConfig settingFileConfig = objectMapper.readValue(settingFile, SettingFileConfig.class);
            return settingFileConfig;
        } catch (IOException e) {
            throw new ConfigParseException("Unable to parse Setting file: " + settingFile, e);
        }
    }

}
