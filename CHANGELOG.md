# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.11.0] - 2021-03-10
### Changed
#### 1. Event Batching

- Added support for batching of events sent to VWO server
- Intoduced withBatchEvents config in launch API for setting when to send bulk events
- Added flushEvents API to manually flush the batch events queue when withBatchEvents config is passed. Note: withBatchEvents config i.e. eventsPerRequest and requestTimeInterval won't be considered while manually flushing
- If requestTimeInterval is passed, it will only set the timer when the first event will arrive
- If requestTimeInterval is provided, after flushing of events, new interval will be registered when the first event will arrive.

```java
  BatchEventData batchData = new BatchEventData();
  batchData.setEventsPerRequest(1000);
  batchData.setRequestTimeInterval(1000);
  batchData.setFlushCallback(new FlushInterface() {
    @Override
    public void onFlush(String s, JsonNode objectNode) {}
  });

  String settingsFile = VWOHelper.getSettingsFile(accountId, sdkKey);
  VWO vwoInstance = VWO.launch(settingsFile).withBatchEvents(batchData).build();

  //(optional): Manually flush the batch events queue to send impressions to VWO server.
  boolean isEventsQueueFlushed = vwoInstance.flushEvents();
```

#### 2. Environment Token

- Send environment token in every network call initiated from SDK to the VWO server. This will help in viewing campaign reports on the basis of environment.

#### 3. Prevent Duplicate Visitor Call

- If User Storage Service is provided, do not track same visitor multiple times.

You can set shouldTrackReturningUser as true in case you prefer to track duplicate visitors.

```java
  VWOAdditionalParams options = new VWOAdditionalParams();
  options.setShouldTrackReturningUser(true);

  vwoInstance.activate(Config.campaignKey, userId, options);
```

Or, you can also set shouldTrackReturningUser at the time of instantiating VWO SDK client. This will avoid passing the flag in different API calls.

```java
String settingsFile = VWOHelper.getSettingsFile(accountId, sdkKey);
VWO vwoInstance = VWO.launch(settingsFile).withShouldTrackReturningUser(true).build();
```

If shouldTrackReturningUser param is passed at the time of instantiating the SDK as well as in the API options as mentioned above, then the API options value will be considered.

#### Correct Usage

- If User Storage Service is provided, campaign activation is mandatory before tracking any goal, getting variation of a campaign, and getting value of the feature's variable.

```java
  vwoInstance.activate(campaignKey, userId, options);
  vwoInstance.track(campaignKey, userId, goalIdentifier, options);
```
#### Wrong Usage

```java
// Calling track API before activate API
// This will not track goal as campaign has not been activated yet.
vwoInstance.track(campaignKey, userId, goalIdentifier, options);

// After calling track APi
vwoInstance.activate(campaignKey, userId, options);
```

## [1.10.0] - 2021-01-21
### Changed
- Webhooks support.
- New API `getAndUpdateSettingsFile` to fetch and update settings-file in case of webhook-trigger.

## [1.8.2] - 2020-06-03
### Changed
-  Added support for polling settingsFile automatically based on the interval provided al the time of using `launch` API

```java
// polling interval is in 'ms'.
String settingsFile = VWOHelper.getSettingsFile(accountId, sdkKey);
VWO vwoInstance = VWO.launch(settingsFile).withPollingInterval(1000).withSdkKey("YOUR_SDK_KEY").build();
```

## [1.8.0] - 2020-04-29
### Changed
- Update track API to handle duplicate and unique conversions and corresponding changes in `launch` API
- Update track API to track a goal globally across campaigns with the same `goalIdentififer` and corresponding changes in `launch` API

```java
// it will track goal having `goalIdentifier` of campaign having `campaignKey` for the user having `userId` as id.
vwoClientInstance.track(campaignKey, userId, goalIdentifier, options);
// it will track goal having `goalIdentifier` of campaigns having `campaignKey1` and `campaignKey2` for the user having `userId` as id.
String[] campaignKeysList = {
  "campaignKey1",
  "campaignKey2",
}
vwoClientInstance.track(campaignKeysList, userId, goalIdentifier, options);
// it will track goal having `goalIdentifier` of all the campaigns
vwoClientInstance.track(null, userId, goalIdentifier, options);
//Read more about configuration and usage - https://developers.vwo.com/reference#server-side-sdk-track
```

## [1.6.1] - 2020-03-12
### Changed
- Prevent updation in user storage in case forced variation is found.

## [1.6.0] - 2020-03-05
### Breaking Changes
To prevent ordered arguments and increasing use-cases, we are moving all optional arguments into a combined argument(Object).

- customVariables argument in APIs: `activate`, `getVariation`, `track`, `isFeatureEnabled`, and `getFeatureVariableValue` have been moved into options object.
- `revenueValue` parameter in `track` API is now moved into `options` argument.

#### Before

```java

// activate API
vwoInstance.activate(campaignKey, userId, customVariables);
// getVariation API
vwoInstance.getVariation(campaignKey, userId, customVariables);
// track API
vwoInstance.track(campaignKey, userId, goalIdentifier, revenueValue, customVariables);
// isFeatureEnabled API
vwoInstance.isFeatureEnabled(campaignKey, userId, customVariables);
// getFeatureVariableValue API
vwoInstance.getFeatureVariableValue(campaignKey, variableKey, userId, customVariables);
```
#### After

```java

// activae API
vwoInstance.activate(campaignKey, userId, options);
// getVariation API
vwoInstance.getVariation(campaignKey, userId, options);
// track API
// Pass revenueValue inside options
vwoInstance.track(campaignKey, userId, goalIdentifier, options);
// isFeatureEnabled API
vwoInstance.isFeatureEnabled(campaignKey, userId, options);
// getFeatureVariableValue API
vwoInstance.getFeatureVariableValue(campaignKey, variableKey, userId, options);
```

### Added
Forced Variation capabilites
- Introduced `Forced Variation` to force certain users into specific variation. Forcing can be based on User IDs or custom variables defined.
### Changed
- All existing APIs to handle custom-targeting-variables as an option for forcing variation
- Code refactored to support Whitelisting. Order of execution

## [1.5.1] - 2019-12-06
### Changed
- Update the user storage and presegment sequence. First try to get from User Storage service, if found, return. Otherwise, check pre-segmentation conditions against user custom-variables

## [1.5.0] - 2019-12-05
### Added
Pre and Post segmentation capabilites
- Introduced new Segmentation service to evaluate whether user is eligible for campaign based on campaign pre-segmentation conditions and passed custom-variables
### Changed
- All existing APIs to handle custom-variables for tageting audience
- Code refactored to support campaign tageting and post segmentation

## [1.4.0] - 2019-11-27
### Added
Feature Rollout and Feature Test capabilities
- Introduced two new APIs i.e. `isFeatureEnabled` and `getFeatureVariableValue`
### Changed
- Existing APIs to handle new type of campaigns i.e. feature-rollout and feature-test
- Code refactored to support feature-rollout and feature-test capabilites

## [1.3.0] - 2019-11-25
### Changed
- Change MIT License to Apache-2.0
- Added apache copyright-header in each file
- Add NOTICE.txt file complying with Apache LICENSE
- Give attribution to the third-party libraries being used and mention StackOverflow

## [1.1.3] - 2019-10-18
### Changed
- Fix passing `r` in custom goal type
- Fix: show error log if goal is revenue type but no revenue value is passed in `track` API

## [1.1.1] - 2019-10-10
### Changed
- First stable release after bug fixes

## [0.1.1] - 2019-10-02
### Added
- First beta release with Server-side A/B capabilities
