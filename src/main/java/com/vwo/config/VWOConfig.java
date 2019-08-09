package com.vwo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vwo.JacksonParser;
import com.vwo.Parser;
import com.vwo.logger.LoggerManager;
import com.vwo.models.Variation;
import com.vwo.models.Campaign;
import com.vwo.models.SettingFileConfig;

import java.util.List;

public class VWOConfig implements ProjectConfig {

    private SettingFileConfig settingFileConfig;
    private final double MAX_TRAFFIC_VALUE = 10000;
    private static final LoggerManager LOGGER = LoggerManager.getLogger(VWOConfig.class);


    public VWOConfig(SettingFileConfig settingFileConfig) {
        this.settingFileConfig = settingFileConfig;
    }

    public SettingFileConfig processSettingsFile() {
        List<Campaign> campaignList = settingFileConfig.getCampaigns();
        for (Campaign singleCampaign : campaignList) {
            setVariationBucketing(singleCampaign);
        }
        return this.settingFileConfig;
    }

    public Campaign setVariationBucketing(Campaign campaign) {
        return setVariationAllocation(campaign);
    }

    public Campaign setVariationAllocation(Campaign campaign) {
        List<Variation> variationList = campaign.getVariations();
        for (Variation singleVariation : variationList) {
            Double stepFactor = getVariationBucketRange(singleVariation.getWeight());
            System.out.println(stepFactor+" step factor");
            if (stepFactor != null && stepFactor != -1) {
                singleVariation.setStartRangeVariation((int)campaign.getCurrentAllocationVariation()+1);
                campaign.setCurrentAllocationVariation(Math.ceil(campaign.getCurrentAllocationVariation() + stepFactor));
                singleVariation.setEndRangeVariation((int)campaign.getCurrentAllocationVariation());
            } else {
                singleVariation.setStartRangeVariation(-1);
                singleVariation.setEndRangeVariation(-1);
            }
            LOGGER.info("Campaign:{} having variations:{} with weight:{} got range as: ( {} - {} )",
                    campaign.getKey(),singleVariation.getName(),singleVariation.getWeight(),
                    singleVariation.getStartRangeVariation(),singleVariation.getEndRangeVariation());
        }
        return campaign;
    }

    public Double getVariationBucketRange(double variationWeight) {
        double startRange = variationWeight * 100;
        System.out.println(startRange+" start range");
        if(startRange==0){
            startRange = -1;
        }
        return Math.min(startRange, MAX_TRAFFIC_VALUE);

    }

    public Campaign getCampaignTestKey(String campaignTestKey) {
        for (Campaign campaign : settingFileConfig.getCampaigns()) {
            if (campaign.getKey().equalsIgnoreCase(campaignTestKey)) {
                LOGGER.trace("CampaignTestKey Found :: ", campaignTestKey);
                return campaign;
            }
        }
        return null;
    }

    public SettingFileConfig getSettingFileConfig() {
        return settingFileConfig;
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
                builder.parser = new JacksonParser(builder.objectMapper);
            }
            return builder;
        }

        public Builder withParser(Parser parser) {
            this.parser = parser;
            return this;
        }

        public VWOConfig build() throws ConfigParseException {
            if (settingFile == null) {
                throw new ConfigParseException("Parsing Failed due to null settings file.");
            }
            if (settingFile.isEmpty()) {
                throw new ConfigParseException("Parsing Failed due to empty settings file.");
            }
            SettingFileConfig settingFileConfig = parser.isValid(settingFile);
            return new VWOConfig(settingFileConfig);
        }
    }

}
