# VWO JAVA SDK

[![CI](https://github.com/wingify/vwo-java-sdk/workflows/CI/badge.svg?branch=master)](https://github.com/wingify/vwo-java-sdk/actions?query=workflow%3ACI)
[![codecov](https://codecov.io/gh/wingify/vwo-java-sdk/branch/master/graph/badge.svg?token=WZ9LNISPPJ)](https://codecov.io/gh/wingify/vwo-java-sdk)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

This open source library allows you to A/B Test your Website at server-side.

## Requirements

The Java SDK supports:

* Open JDK 8, 9, 11, 12
* Oracle JDK 8, 9, 11, 12

## SDK Installation

Install dependencies using 'mvn install'

Add below Maven dependency in your project.


```java
<dependency>
    <groupId>com.vwo.sdk</groupId>
    <artifactId>vwo-java-sdk</artifactId>
    <version>LATEST</version>
</dependency>
```

## Documentation

Refer [Official VWO Documentation](https://developers.vwo.com/docs/fullstack-overview)

## Basic Usage

**GET SETTINGS FILE**

Each VWO SDK client corresponds to the settingsFIle representing the current state of the campaign settings, that is, a list of server-side running campaign settings.
Setting File is a pre-requisite for initiating the VWO CLIENT INSTANCE.

```java
String settingsFile = VWO.getSettingsFile(accountId, sdkKey));
```

**INSTANTIATION**


SDK provides a method to instantiate a VWO client as an instance. The method accepts an object to configure the VWO client.
The mandatory parameter for instantiating the SDK is settingsFile.

```java
import com.vwo.VWO;

VWO vwoInstance = VWO.launch(settingsFile).build();
```

**Activate API**

```java
String variationName = vwoClientInstance.activate(campaignKey, userId, options);

if (variationName.equals("Control")) {
  // Write code for handling 'Control'
} else if (variationName.equals("Variation-1")) {
  // CODE: write code for Variation-1
} else {
  // CODE: When user does not become part of campaign.
}
```

**Track API**

```java
// For CUSTOM goal type
boolean isSuccessful = vwoClientInstance.track(campaignKey, userId, goalIdentifier, options);

// For REVENUE goal type
boolean isSuccessful = vwoClientInstance.track(campaignKey, userId, goalIdentifier, options);
```

**Feature Enabled API**

```java
Boolean isEnabled = vwoClientInstance.isFeatureEnabled(campaignKey, userId, options);

if (isEnabled) {
  // Write code for handling feature enabled
} else {
  // CODE: User is not qualified for the campaign. Would be due to configuring campaign's percent-traffic less than 100% while creating or updating a FullStack campaign.
}
```

**Get Variable Value API**

```java
Object value = vwoClientInstance.getFeatureVariableValue(campaignKey, variableKey, userId, options);
```

**Push API**

```java
boolean isSuccessful = vwoClientInstance.push(customDimensionKey, customDimensionValue, userId);
```

The VWO client class needs to be instantiated as an instance that exposes various API methods like activate, getVariation and track.

**USER STORAGE SERVICE**

```java
String settingsFile = VWO.getSettingsFile(accountId, sdkKey);

Storage.User userStorage = return new Storage.User() {
     @Override
     public Map<String, String> get(String userId, String campaignName) {
        for (Map<String, String> savedCampaign: campaignStorageArray) {
            if (savedCampaign.get("userId").equals(userId) && savedCampaign.get("campaignKey").equals(campaignName)) {
               return savedCampaign;
            }
        }
        return null;
     }

     @Override
     public void set(Map<String, String> map){
        campaignStorageArray.add(map);
     }
};

VWO vwo = VWO.launch(settingsFile).withUserStorage(userStorage).build();
```

**LOGGER**

JAVA SDK utilizes a logging facade, SL4J (https://www.slf4j.org/) as the logging api layer. If no binding is found on the class path,
then you will see the following logs

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
You can get the SDK to log by providing a concrete implementation for SLF4J.
```

What it means is that at runtime, the logging `implementation` (or the logger binding) is missing , so slf4j simply use a "NOP" implementation, which does nothing.
If you need to output JAVA SDK logs, there are different approaches for the same.

SIMPLE IMPLEMENTATION
If there are no implementation in your project , you may provide a simple implementation that does not require any configuration at all.
Add following code to you pom.xml,

```java
 <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.6.4</version>
</dependency>
```

Now you see logging output on STDOUT with INFO level. This simple logger will default show any INFO level message or higher.
In order to see DEBUG messages, you would need to pass -Dorg.slf4j.simpleLogger.defaultLogLevel=debug or simplelogger.properties file on the classpath
See http://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html for details

CONCRETE IMPLEMENTATION

sl4j supports various logging framework. Refer here ->https://www.slf4j.org/manual.html

We have provided our example with Logback
If you have logback in your class path, to get console logs add following Appender and Logger in logback for formatted logs.

```java
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <Pattern>
            %cyan(VWO-SDK) [%date] %highlight([%level]) %cyan([%logger{10} %file:%line]) %msg%n
        </Pattern>
    </encoder>

    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>DEBUG</level>
    </filter>
</appender>

<Logger name="com.vwo.sdk" additivity="false">
    <appender-ref ref="STDOUT"/>
</Logger>
```

```java
new VWOLogger(VWO.Enums.LOGGER_LEVEL.DEBUG.value()) {
    @Override
    public void trace(String message, Object... params) {
        LOGGER.trace(message, params);
    }

    @Override
    public void debug(String message, Object... params) {
        LOGGER.debug(message, params);
    }

    @Override
    public void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    @Override
    public void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }

    @Override
    public void error(String message, Object... params) {
        LOGGER.error(message, params);
    }
};
```

For more appenders, refer [this](https://logback.qos.ch/manual/appenders.html).

## Third-party Resources and Credits

Refer [third-party-attributions.txt](https://github.com/wingify/vwo-java-sdk/blob/master/third-party-attributions.txt)

## Authors

* Core Contributor & Maintainer - [pntgupta](https://github.com/pntgupta)
* Main Contributor - [sakshimahendruvk](https://github.com/sakshimahendruvk)
* Repo health maintainer - [softvar](https://github.com/softvar)

## Changelog

Refer [CHANGELOG.md](https://github.com/wingify/vwo-java-sdk/blob/master/CHANGELOG.md)

## Contributing

Please go through our [contributing guidelines](https://github.com/wingify/vwo-java-sdk/blob/master/CONTRIBUTING.md)

## Code of Conduct

[Code of Conduct](https://github.com/wingify/vwo-java-sdk/blob/master/CODE_OF_CONDUCT.md)

## License

[Apache License, Version 2.0](https://github.com/wingify/vwo-java-sdk/blob/master/LICENSE)

Copyright 2019-2021 Wingify Software Pvt. Ltd.
