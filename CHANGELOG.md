# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
