# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.8.2] - 2020-06-03
### Changes
-  Added support for polling settingsFile automatically based on the interval provided al the time of using `launch` API

```java
// polling interval is in 'ms'.
String settingsFile = VWOHelper.getSettingsFile(accountId, sdkKey);
VWO vwoInstance = VWO.launch(settingsFile).withPollingInterval(1000).withSdkKey("YOUR_SDK_KEY").build();
```

## [1.8.0] - 2020-04-29
### Changes
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