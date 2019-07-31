**VWO JAVA SDK**

This open source library allows you to A/B Test your Website at server-side.

**QUICK SETUP**


**Language Support**
The Java SDK supports JDK8 and later.

**SDK INSTALLATION**
ADD BELOW MAVEN DEPENDENCY TO YOUR PROJECT

``` 
        <dependency>
        
			<groupId>com.wingify.vwo</groupId>
			
			<artifactId>vwo-java-sdk</artifactId>
			
			<version>1.0-SNAPSHOT</version>
			
		</dependency>
```
**GET SETTING FILE**

Each VWO SDK client corresponds to the settingsFIle representing the current state of the campaign settings, that is, a list of server-side running campaign settings.
Setting File is a pre-requisite for initiating the VWO CLIENT INSTANCE.

```

  String settingsFle= FileSettingUtils.getSetting(accountId, sdkKey));
  
```

Description
VWO SDK is a helper for running your server-side A/B tests. It requires a certain set of settings for it's working. These settings are related to your server-side campaigns you create or update in the VWO application.
So, before instantiating the VWO SDK, settingsFile has to be fetched.

```

The method accepts two parameters:

accountId - account-id associated to your VWO account.
sdkKey - provided to you inside VWO Application under the Apps section of server-side A/B Testing. Read more on how to get the sdk-key for your project.

```

Returns
JSON representation String representing the current state of campaign settings

**INSTANTIATION**


SDK provides a method to instantiate a VWO client as an instance. The method accepts an object to configure the VWO client.
The mandatory parameter for instantiating the SDK is settingsFile.

```

import com.wingify.vwo.VWO;

  VWO vwo_instance = VWO.createInstance(settingsFle).build();
  
```

The VWO client class needs to be instantiated as an instance that exposes various API methods like activate, getVariation and track.

** ASYN EVENT DISPATCHER **

```java
import com.vwo.event.EventDispatcher;
import com.vwo.config.FileSettingUtils;


public class Example {

    private final VWO vwo;

    public Example(VWO vwo) {
        this.vwo = vwo;
    }

    public static void main(String[] args) {

        String settingsFle = FileSettingUtils.getSetting("60781", "ea87170ad94079aa190bc7c9b85d26fb");

        // EventDispatcher(int eventQueueSize, int corePoolSize, int maxPoolSize, long closeTimeout, TimeUnit closeTimeoutUnit)
        EventDispatcher eventDispatcher = new EventDispatcher();

        VWO vwo_instace = VWO.createInstance(settingsFle).withEventHandler(eventDispatcher).build();

//            EventDispatcher eventDispatcher = new EventDispatcher(10000,2,200,
//                    Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
}
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

What it means is that at runtime, the logging "implementation" (or the logger binding) is missing , so slf4jsimply use a "NOP" implmentation, which does nothing.
If you need to output JAVA SDK logs , there are different approaches for the same.

SIMPLE IMPLEMENTATION
If there are no implementation in your project , you may provide a simple implementation that does not require any configuration at all.
Add following code to you pom.xml,

```
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

```
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

    <Logger name="com.wingify.vwo" additivity="false">
        <appender-ref ref="STDOUT"/>
    </Logger>
```

For more appenders ,refer https://logback.qos.ch/manual/appenders.html

## Authors

[sakshimahendruvk] (https://github.com/sakshimahendruvk)



