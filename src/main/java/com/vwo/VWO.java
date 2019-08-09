package com.vwo;

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;


import com.vwo.bucketing.Bucketer;
import com.vwo.config.FileSettingUtils;
import com.vwo.enums.LoggerMessagesEnum;
import com.vwo.event.DispatchEvent;
import com.vwo.event.EventDispatcher;
import com.vwo.event.EventHandler;
import com.vwo.models.Goal;
import com.vwo.models.Variation;
import com.vwo.config.ConfigParseException;
import com.vwo.config.ProjectConfig;
import com.vwo.config.VWOConfig;
import com.vwo.event.EventFactory;
import com.vwo.bucketing.BucketingService;
import com.vwo.models.Campaign;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vwo.userprofile.UserProfileService;
import org.springframework.lang.NonNull;

/**
 * The VWO client class needs to be instantiated as an instance that exposes various API methods like activate, getVariation and track.
 */
public class VWO implements AutoCloseable {

    final ProjectConfig projectConfig;
    final UserProfileService userProfileService;
    final EventHandler eventHandler;
    final BucketingService bucketingService;
    private boolean developmentMode;

    private static final Logger LOGGER = LoggerFactory.getLogger(VWO.class);

    private VWO(@NonNull ProjectConfig projectConfig,
                @NonNull UserProfileService userProfileService,
                @NonNull EventHandler eventHandler,
                @NonNull BucketingService bucketingService,
                @NonNull boolean developmentMode) {

        this.projectConfig = projectConfig;
        this.userProfileService = userProfileService;
        this.eventHandler = eventHandler;
        this.bucketingService = bucketingService;
        this.developmentMode = developmentMode;
    }


    public ProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public UserProfileService getUserProfileService() {
        return userProfileService;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public BucketingService getBucketingService() {
        return bucketingService;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    /**
     * Fetch the account settings.
     *
     * @param accountID VWO application account-id.
     * @param sdkKey Unique sdk-key provided to you inside VWO Application under the Apps section of server-side A/B Testing
     * @return JSON representation String representing the current state of campaign settings
     */
    public static String getSetting(String accountID, String sdkKey) {
        return FileSettingUtils.getSetting(accountID, sdkKey);
    }

    /**
     *
     * @param developmentMode
     */
    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    /**
     * validates the parameters passed
     * checks whether the user qualifies to become a part of the campaign
     * assigns a deterministic variation to the qualified user
     * sends an impression event to the VWO server for generating reports
     *
     * @param campaignTestKey
     * @param userId
     * @return String name of the variation in which the user is bucketed, or null if the user doesn't qualify to become a part of the campaign.
     */
    public String activate(String campaignTestKey, String userId) {
        LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.INITIATING_ACTIVATE.value(new Pair<>("userId", userId), new Pair<>("campaignTestKey", campaignTestKey)));

        if (campaignTestKey == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_CAMPAIGN_KEY.value());
            return null;
        }
        if (userId == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_USER_ID.value());
            return null;
        }
        if (this.projectConfig == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_PROJECT_CONFIG.value());
            return null;
        }

        Campaign campaign = this.projectConfig.getCampaignTestKey(campaignTestKey);

        if (campaign == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value());
            return null;
        }

        return this.activateCampaign(campaign, userId, Collections.<String, String>emptyMap());
    }

    /**
     *
     * @param campaign
     * @param userId
     * @param userProfileMap
     * @return
     */
    private String activateCampaign(Campaign campaign, String userId, Map<String, ?> userProfileMap) {
        String variation = this.getCampaignVariation(campaign, userId, userProfileMap);

        if (variation != null) {
            LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.ACTIVATING_CAMPAIGN.value(new Pair<>("userId", userId), new Pair<>("variation", variation)));

            // Send Impression Call for Stats
            if(!this.isDevelopmentMode()) {
                this.sendImpressionCall(this.projectConfig, campaign, userId, CampaignUtils.getVariationObjectFromCampaign(campaign, variation));
            }
        } else {
            LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.NO_VARIATION_ALLOCATED.value(new Pair<>("userId", userId), new Pair<>("campaignTestKey", campaign.getKey())));
        }
        return variation;
    }

    /**
     *  getVariation API activates a server-side A/B test for the specified user for a particular running campaign.
     *  validates the parameters passed
     * checks whether the user qualifies to become a part of the campaign
     * assigns a deterministic variation to the qualified user
     * does not send an impression event to the VWO server
     * @param campaignTestKey key provided at the time of server-side campaign creation
     * @param userId unique id associated with the user for identification
     * @return
     */
    public String getVariation(String campaignTestKey, String userId) {
        LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.INITIATING_GET_VARIATION.value(new Pair<>("userId", userId), new Pair<>("campaignTestKey", campaignTestKey)));

        if (campaignTestKey == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_CAMPAIGN_KEY.value());
            return null;
        }
        if (userId == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_USER_ID.value());
            return null;
        }
        if (this.projectConfig == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_PROJECT_CONFIG.value());
            return null;
        }
        Campaign campaign = this.projectConfig.getCampaignTestKey(campaignTestKey);

        if (campaign == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value());
        }

        return this.getCampaignVariation(campaign, userId, Collections.<String, String>emptyMap());
    }

    /**
     *
     * @param campaign
     * @param userId
     * @param userProfileMap
     * @return
     */
    private String getCampaignVariation(Campaign campaign, String userId, Map<String, ?> userProfileMap) {
        Variation variation = this.bucketingService.getVariation(campaign, userId);
        return variation != null ? variation.getName() : null;
    }

    /**
     * Tracks a conversion event for a particular user for a running server-side campaign
     * validates the parameters passed
     * assigns the same variation to the same qualified user
     * sends an impression event to the VWO server for generating reports
     * @param campaignTestKey key provided at the time of server-side campaign creation.
     * @param userId unique id associated with the user for identification
     * @param goalIdentifier key provided at the time of creating the goal in the server-side
     * @return
     */
    public boolean track(String campaignTestKey, String userId, String goalIdentifier) {
        if (campaignTestKey == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_CAMPAIGN_KEY.value());
            return false;
        }
        if (userId == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_USER_ID.value());
            return false;
        }
        if (this.projectConfig == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.MISSING_PROJECT_CONFIG.value());
            return false;
        }

        Campaign campaign = this.projectConfig.getCampaignTestKey(campaignTestKey);

        if (campaign == null) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.CAMPAIGN_NOT_FOUND.value());
            return false;
        }
        return this.trackGoal(campaign, userId, goalIdentifier, Collections.<String, String>emptyMap());
    }

    /**
     *
     * @param campaign
     * @param userId
     * @param goalIdentifier
     * @param userProfileMap
     * @return
     */
    private boolean trackGoal(Campaign campaign, String userId, String goalIdentifier, Map<String, ?> userProfileMap) {
        String variation = this.getCampaignVariation(campaign, userId, userProfileMap);

        if (variation != null) {
            Goal goal = this.getGoalId(campaign, goalIdentifier);

            if (goal == null) {
                LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.TRACK_API_GOAL_NOT_FOUND.value(new Pair<>("userId", userId), new Pair<>("campaignTestKey", campaign.getKey())));
                return false;
            }

            if(!isDevelopmentMode()) {
                this.sendConversionCall(this.projectConfig, campaign, userId, goal, CampaignUtils.getVariationObjectFromCampaign(campaign, variation));
            }

            return true;
        } else {
            LOGGER.info(LoggerMessagesEnum.INFO_MESSAGES.TRACK_API_VARIATION_NOT_FOUND.value(new Pair<>("userId", userId), new Pair<>("campaignTestKey", campaign.getKey())));
        }

        return false;
    }

    /**
     *
     * @param campaign
     * @param goalIdentifier
     * @return
     */
    private Goal getGoalId(Campaign campaign, String goalIdentifier) {
        for (Goal singleGoal : campaign.getGoals()) {
            if (goalIdentifier.equalsIgnoreCase(singleGoal.getIdentifier())) {
                return singleGoal;
            }
        }
        return null;
    }

    /**
     *
     * @param projectConfig
     * @param campaign
     * @param userId
     * @param variation
     */
    private void sendImpressionCall(ProjectConfig projectConfig, Campaign campaign, String userId, Variation variation) {
        DispatchEvent dispatchEvent = EventFactory.createImpressionLogEvent(projectConfig, campaign, userId, variation);
        try {
            eventHandler.dispatchEvent(dispatchEvent);
        } catch (Exception e) {
            LOGGER.error("Unexpected exception in event dispacther");
        }
    }

    /**
     *
     * @param projectConfig
     * @param campaign
     * @param userId
     * @param goal
     * @param variation
     */
    private void sendConversionCall(ProjectConfig projectConfig, Campaign campaign, String userId, Goal goal, Variation variation) {
        DispatchEvent dispatchEvent = EventFactory.createGoalLogEvent(projectConfig, campaign, userId, goal, variation);
        try {
            eventHandler.dispatchEvent(dispatchEvent);
        } catch (Exception e) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.UNABLE_TO_DISPATCH_EVENT.value());
        }
    }

    /**
     *
     * @param settingFile
     * @return
     */
    public static Builder createInstance(String settingFile) {
        try {
            return new Builder().withSettingFile(settingFile);
        } catch (Exception e) {
            LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.GENERIC_ERROR.value(), e.getStackTrace());
        }
        return null;
    }

    /**
     *
     */
    public static class Builder {

        private ProjectConfig projectConfig;
        private UserProfileService userProfileService;
        private EventHandler eventHandler;
        private BucketingService bucketingService;
        private String settingFile;
        private Bucketer bucketer;
        private boolean developmentMode;


        private Builder withSettingFile(String settingFile) {
            this.settingFile = settingFile;
            return this;
        }

        public Builder withUserProfileService(UserProfileService userProfileService) {
            this.userProfileService = userProfileService;
            return this;
        }

        public Builder withEventHandler(EventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public Builder withDevelopmentMode(boolean developmentMode) {
            this.developmentMode = developmentMode;
            return this;
        }

        public VWO build() {
            if (this.projectConfig == null && this.settingFile != null && !this.settingFile.isEmpty()) {
                try {
                    this.projectConfig = VWOConfig.Builder.getInstance(this.settingFile).build();
                } catch (ConfigParseException e) {
                    LOGGER.error(LoggerMessagesEnum.ERROR_MESSAGES.GENERIC_ERROR.value(), e);
                }

                this.projectConfig.processSettingsFile();
                LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.SETTINGS_FILE_PROCESSED.value());
            }

            if (this.eventHandler == null) {
                this.eventHandler = EventDispatcher.builder().build();
            }

            this.bucketer = new Bucketer();
            this.bucketingService = new BucketingService(bucketer, userProfileService);
            this.developmentMode = this.developmentMode || false;

            // process SettingsFile
            VWO vwo_instance = new VWO(this.projectConfig, this.userProfileService, this.eventHandler, this.bucketingService, this.developmentMode);
            if (vwo_instance != null) {
                LOGGER.debug(LoggerMessagesEnum.DEBUG_MESSAGES.SDK_INITIALIZED.value());
            }
            return vwo_instance;
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     *
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     *
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     *
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        tryClose(this.eventHandler);
        tryClose(this.projectConfig);
    }


    public void tryClose(Object obj) {
        if (!(obj instanceof AutoCloseable)) {
            return;
        }
        try {
            ((AutoCloseable) obj).close();
        } catch (Exception e) {
            LOGGER.warn(LoggerMessagesEnum.WARNING_MESSAGES.CLOSE_GENERIC_CONNECTION.value(), obj);
        }
    }


}
