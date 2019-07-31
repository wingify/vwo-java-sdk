package com.vwo;

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;


import com.vwo.bucketing.Bucketer;
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
     * @param campaignTestKey
     * @param userId
     * @return String name of the variation in which the user is bucketed, or null if the user doesn't qualify to become a part of the campaign.
     */
    public String activate(String campaignTestKey, String userId) {
        return activate(campaignTestKey, userId, Collections.<String, String>emptyMap());
    }

    /**
     * @param campaignTestKey key provided at the time of server-side campaign creation
     * @param userId unique id associated with the user for identification
     * @param userProfileMap
     * @return
     */
    public String activate(String campaignTestKey, String userId, Map<String, ?> userProfileMap) {
        if (campaignTestKey == null) {
            LOGGER.error("The campaignTestKey should be NonNull. Returning null...");
            return null;
        }
        if (userId == null) {
            LOGGER.error("The userId should be NonNull. Returning null...");
            return null;
        }
        if (this.projectConfig == null) {
            LOGGER.error("Not a valid VWO Instance, Failing activate call ...");
            return null;
        }
        Campaign campaign = this.projectConfig.getCampaignTestKey(campaignTestKey);
        if (campaign == null) {
            LOGGER.error("Unable to return the CampaignTestKey. Please verify the Campaign Test Key.");
            return null;
        }

        return activate(campaign, userId, userProfileMap);
    }

    /**
     *
     * @param campaign
     * @param userId
     * @param userProfileMap
     * @return
     */
    private String activate(Campaign campaign, String userId, Map<String, ?> userProfileMap) {

        if (!isValidUser(userId)) {
            LOGGER.error("Activate Call Failed for Campaign{}. \n  Invalid UserId {} ", campaign.getKey(), userId);
            return null;
        }

        String variation = getVariation(campaign, userId, userProfileMap);
        if (variation != null) {
            LOGGER.debug("Activating userId {}  for Variation {}!!", userId, variation);

            //send Impression Call for Stats
            // check for config null
            if(!isDevelopmentMode()) {
                sendImpressionCall(projectConfig, campaign, userId, CampaignUtils.getVariationObjectFromCampaign(campaign,variation));
            }
            return variation;
        } else {
            LOGGER.info("Not Activating UserId {} for Campaign {}", userId, campaign.getKey());
        }
        return null;
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
        if (campaignTestKey == null) {
            LOGGER.error("The campaignTestKey should be NonNull. Returning null...");
            return null;
        }
        if (userId == null) {
            LOGGER.error("The userId should be NonNull. Returning null...");
            return null;
        }
        if (this.projectConfig == null) {
            LOGGER.error("Not a valid VWO Instance, Failing activate call ...");
            return null;
        }
        Campaign campaign = this.projectConfig.getCampaignTestKey(campaignTestKey);
        if (campaign == null) {
            LOGGER.error("Unable to return the CampaignTestKey. Please verify the Campaign Test Key.");
        }

        return getVariation(campaign, userId, Collections.<String, String>emptyMap());
    }

    /**
     *
     * @param campaign
     * @param userId
     * @param userProfileMap
     * @return
     */
    public String getVariation(Campaign campaign, String userId, Map<String, ?> userProfileMap) {
        Variation variation = bucketingService.getVariation(campaign, userId);
        if(variation!=null){
        return variation.getName();
        }
       return null;
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
        return track(campaignTestKey, userId, goalIdentifier, Collections.<String, String>emptyMap());
    }

    /**
     *
     * @param campaignTestKey
     * @param userId
     * @param goalIdentifier
     * @param userProfileMap
     * @return
     */
    public boolean track(String campaignTestKey, String userId, String goalIdentifier, Map<String, ?> userProfileMap) {
        if (campaignTestKey == null) {
            LOGGER.error("The campaignTestKey should be NonNull. Returning null...");
            return false;
        }
        if (userId == null) {
            LOGGER.error("The userId should be NonNull. Returning null...");
            return false;
        }
        if (this.projectConfig == null) {
            LOGGER.error("Not a valid VWO Instance, Failing activate call ...");
            return false;
        }

        Campaign campaign = this.projectConfig.getCampaignTestKey(campaignTestKey);
        if (campaign == null) {
            LOGGER.error("Unable to return the CampaignTestKey. Please verify the Campaign Test Key.");
            return false;
        }
        return track(campaign, userId, goalIdentifier, userProfileMap);
    }

    /**
     *
     * @param campaign
     * @param userId
     * @param goalIdentifier
     * @param userProfileMap
     * @return
     */
    private boolean track(Campaign campaign, String userId, String goalIdentifier, Map<String, ?> userProfileMap) {
        if (!isValidUser(userId)) {
            LOGGER.error("Activate Call Failed for Campaign {}. \n  Invalid UserId {} ", campaign.getKey(), userId);
            return false;
        }

        String variation = getVariation(campaign, userId, userProfileMap);

        if (variation != null) {

            LOGGER.debug("Activating userId {}  for Variation {}!!", userId, variation);

            //send Impression Call for Stats
            // check for config null
            Goal goal = getGoalId(campaign, goalIdentifier);

            if (goal == null) {
                LOGGER.info("Not Tracking UserId {} for Campaign {} and Goal {} ", userId, campaign.getKey(), goalIdentifier);
                return false;
            }

            if(!isDevelopmentMode()) {
                sendConversionCall(projectConfig, campaign, userId, goal, CampaignUtils.getVariationObjectFromCampaign(campaign,variation));
            }

        } else {
            LOGGER.info("Not Activating UserId {} for Campaign {}", userId, campaign.getKey());
        }
        return true;
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
            LOGGER.error("Unexpected exception in event dispacther");
        }
    }


    /**
     *
     * @param userId
     * @return
     */
    private boolean isValidUser(String userId) {
        if (userId == null) {
            LOGGER.error("The user ID should be NonNull");
            return false;
        }
        return true;
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
            LOGGER.error("Unexpected Exception", e.getStackTrace());
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
        // TO DO
//        public Builder withtProjectConfigManager(ProjectConfig projectConfig) {
//            this.projectConfig = projectConfig;
//            return this;
//        }

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



        // TO DO
//        public Builder withLoggerConfigManager(LoggerConfigManager loggerConfigMnaager) {
//            this.loggerConfigManager = loggerConfigMnaager;
//            return this;
//        }
//
//        public Builder withLogicService(BucketingService bucketingService) {
//            this.bucketingService = bucketingService;
//            return this;
//        }
//
//        public Builder withBucketer(Bucketer bucketer) {
//            this.bucketer = bucketer;
//            return this;
//        }

        public VWO build() {
            //check validity
            if (projectConfig == null && settingFile != null && !settingFile.isEmpty()) {
                try {
                    projectConfig = VWOConfig.Builder.getInstance(settingFile).build();
                } catch (ConfigParseException e) {
                    e.printStackTrace();
                }
                projectConfig.processSettingsFile();
                LOGGER.info("Settings File Successfully Loaded!!!");
            }

            if (eventHandler == null) {
                eventHandler = new EventDispatcher();
            }

            if (bucketingService == null) {
                if (bucketer == null) {
                    bucketer = new Bucketer();
                    bucketingService = new BucketingService(bucketer, userProfileService);
                }
            }


            if (!developmentMode) {
                developmentMode = false;
            }

            // process SettingsFile
            VWO vwo_instance = new VWO(projectConfig, userProfileService, eventHandler, bucketingService, developmentMode);
            if (vwo_instance != null) {
                LOGGER.info("SDK properly initialized");
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
        tryClose(eventHandler);
        tryClose(projectConfig);

    }


    public void tryClose(Object obj) {
        if (!(obj instanceof AutoCloseable)) {
            return;
        }
        try {
            ((AutoCloseable) obj).close();
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception on trying to close {}.", obj);
        }
    }


}
