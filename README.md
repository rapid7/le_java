[![Build Status](https://travis-ci.org/logentries/le_java.png?branch=master)](https://travis-ci.org/logentries/le_java)

Logging To Logentries from Java
==============================

Logentries currently supports logging from Java using the following logging libraries:

* [Log4J](https://github.com/logentries/le_java#log4j)
* [Log4J2](https://github.com/logentries/le_java#log4j2)
* [Logback](https://github.com/logentries/le_java#logback)

--------------------------------------------------------------

Account Setup
-------------
You can sign up for a Logentries account simply by clicking Sign Up and entering your email address. Once you have your credentials and have logged in,
create a new host in the UI with a name that represents your app. Then, select this host and create a new logfile with a name that represents what you're
logging. Select 'TOKEN TCP' as the source_type and click Register to create the log.

--------------------------------------------------------------

LOG4J
=====

To configure log4j, you will need to perform the following:

    * (1) Install Log4j (if you are not already using it).
    * (2) Install the Logentries Log4j plugin.
    * (3) Configure the Logentries Log4j plugin.

Maven Users
-----------

Place this in your pom.xml

    <dependencies>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <dependency>
            <groupId>com.logentries</groupId>
            <artifactId>logentries-appender</artifactId>
            <version>RELEASE</version>
        </dependency>
    </dependencies>

Manual Install
--------------

Download log4j from:

https://logging.apache.org/log4j/1.2/download.html

Retrieve log4j jar file and place it the `WEB-INF/lib` folder of your project.

Then add it to the build path from within your project.

The next file you need is logentriesappender-1.1.27.jar which is the plugin for log4j. You can get it <a href="http://search.maven.org/remotecontent?filepath=com/logentries/logentries-appender/1.1.27/logentries-appender-1.1.27.jar">here.</a>

Place this in the `WEB-INF/lib` folder of your project and add it to the buildpath as done above with log4j jar.

Configure the Log4J plugin
--------------------------

Download the required log4j.xml config file from <a href="https://github.com/logentries/le_java/raw/master/configFiles/log4j.xml.example">here</a>

Add this file to your project as it is the config which adds the plugin for log4j to send logs to Logentries. This file should be in added to the classpath.

In this file, you will see the following:

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
    <log4j:configuration debug="true">
    <appender name="le" class="com.logentries.log4j.LogentriesAppender">
        <!-- Enter your Logentries token, like bc0c4f90-a2d6-11e1-b3dd-0800200c9a66 -->
        <!-- Or set an evironment variable like LOGENTRIES_TOKEN=bc0c4f90-a2d6-11e1-b3dd-0800200c9a66 -->
        <param name="Token" value="LOGENTRIES_TOKEN" />
        <param name="Debug" value="false" />
        <param name="Ssl" value="false" />
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                value="%d{yyyy-MM-dd HH:mm:ss ZZZ} %-5p (%F:%L)  %m" />
        </layout>
    </appender>
    <logger name="example">
        <level value="debug" />
    </logger>
    <root>
        <priority value="debug"></priority>
        <appender-ref ref="le" />
    </root>
    </log4j:configuration>

Replace the value "LOGENTRIES_TOKEN" with the token UUID that is to the right of your newly created logfile.  Alternatively leave the Token entry empty in the log4j configuration and provide the token via an environment variable e.g., `export LOGENTRIES_TOKEN=bc0c4f90-a2d6-11e1-b3dd-0800200c9a66`.  This approach makes it easy to provide different logging tokens without repackaging when moving an app through dev, test, and prod etc.

For debugging purposes set the debug parameter to true.

DataHub Logging
---------------

To log to a DataHub we can change log4j.xml configuration to send logs to your instance of DataHub.

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
    <log4j:configuration debug="true">
    <appender name="le" class="com.logentries.log4j.LogentriesAppender">
        <!-- Enter your Logentries token, like bc0c4f90-a2d6-11e1-b3dd-0800200c9a66 -->
        <!-- Or set an evironment variable like LOGENTRIES_TOKEN=bc0c4f90-a2d6-11e1-b3dd-0800200c9a66 -->
        <param name="Token" value="LOGENTRIES_TOKEN" />
        <param name="Debug" value="false" />
        <param name="Ssl" value="false" />
		<param name="IsUsingDataHub" value="true"/>
		<param name="DataHubAddr" value="localhost"/>
		<param name="DataHubPort" value="10000"/>
		<param name="LogHostName" value="true"/>
		<param name="HostName" value="TestHost*001"/>
		<param name="LogID" value="JavaTestID"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                value="%d{yyyy-MM-dd HH:mm:ss ZZZ} %-5p (%F:%L)  %m" />
        </layout>
    </appender>
    <logger name="example">
        <level value="debug" />
    </logger>
    <root>
        <priority value="debug"></priority>
        <appender-ref ref="le" />
    </root>
    </log4j:configuration>

The extra parameters are the following,

	<param name="IsUsingDataHub" value="true"/>: Sent to a DataHub instance if true.
	<param name="DataHubAddr" value="localhost"/>: The IP of the DataHub instance that we will connect to.
	<param name="DataHubPort" value="10000"/>: The Port of the DataHub instance that we will connect to.
	<param name="LogHostName" value="true"/>: Prefixes log messages with a HostName
	<param name="HostName" value="TestHost*001"/>: The HostName to prefix each log message with. If not set will be automatically detected.
	<param name="LogID" value="JavaTestID"/>: The LogID to be prefixed with each log message. If not set it will not be logged.


Logging Messages
----------------

With that done, you are ready to send logs to Logentries.

In each class you wish to log from, enter the following using directives at the top if not already there:

    import org.apache.log4j.Logger;
    import org.apache.log4j.LogManager;

Then create this object at class-level:

    private static Logger log = LogManager.getRootLogger();

Now within your code in that class, you can log using log4j as normal and it will log to Logentries.

Example:

    log.debug("Debugging Message");
    log.info("Informational message");
    log.warn("Warning Message");

LOG4J2
======

To configure log4j2, you will need to perform the following:

    * (1) Install Log4j2 (if you are not already using it).
    * (2) Install the Logentries appender.
    * (3) Configure the Logentries appender.

Maven Users
-----------

Place this in your pom.xml

    <dependency>
		<groupId>org.apache.logging.log4j</groupId>
		<artifactId>log4j-api</artifactId>
		<version>2.1</version>
	</dependency>
	<dependency>
		<groupId>org.apache.logging.log4j</groupId>
		<artifactId>log4j-core</artifactId>
		<version>2.1</version>
	</dependency>
    <dependency>
		<groupId>com.logentries</groupId>
		<artifactId>logentries-appender</artifactId>
		<version>RELEASE</version>
	</dependency>

Manual Install
--------------

Download log4j2 from:

http://logging.apache.org/log4j/2.0/download.html

Retrieve log4j2 jar file and place it the `WEB-INF/lib` folder of your project.

Then add it to the build path from within your project.

The next file you need is logentriesappender-{VERSION}.jar which is the appender for log4j2. You can get it <a href="http://search.maven.org/remotecontent?filepath=com/logentries/logentries-appender/1.1.29/logentries-appender-1.1.29.jar">here.</a>

Place this in the `WEB-INF/lib` folder of your project and add it to the buildpath as done above with log4j2 jar.

Configure the Log4J2 plugin
--------------------------

Create a log4j2.xml file and include at least the following to log to logentries:

    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN">
		<Appenders>
    		<Logentries >
    			<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss ZZZ} %F.%L level:%-5p%m"/>
    			<name>le</name>
    			<token>LOGENTRIES_TOKEN</token>
    		</Logentries>
    	</Appenders>
    
    	<Loggers>
    		<Root level="INFO">
    			<AppenderRef ref="le" />
    		</Root>
    	</Loggers>
    </Configuration>

Replace the value "LOGENTRIES_TOKEN" with the token UUID that is to the right of your newly created logfile.  Alternatively leave the Token entry empty in the log4j2 configuration and provide the token via an environment variable e.g., `export LOGENTRIES_TOKEN=bc0c4f90-a2d6-11e1-b3dd-0800200c9a66`.  This approach makes it easy to provide different logging tokens without repackaging when moving an app through dev, test, and prod etc.

Maven users may put the file in their src/main/resources folder.

DataHub Logging
---------------

To log to a DataHub we can change log4j2.xml configuration to send logs to your instance of DataHub.

	<?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN">
    	<Appenders>
    		<Logentries >
    			<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss ZZZ} %F.%L level:%-5p%m"/>
    			<name>le</name>
    			<token>LOGENTRIES_TOKEN</token>
    			<debug>false</debug>
    			<ignoreExceptions>false</ignoreExceptions>
    			<!-- datahub specific options -->
    			<logId></logId>
    			<key>account_key</key>
    			<useDataHub>true</useDataHub>
    			<dataHubAddr>localhost</dataHubAddr>
    			<location>my_datacentre</location>
    			<dataHubPort>10000</dataHubPort>
    			<logHostName>true</logHostName>
    			<hostName>my_host</hostName>
    			<logId>log_id</logId>
    			<httpPut>false</httpPut>
    		</Logentries>
    	</Appenders>
    
    	<Loggers>
    		<Root level="INFO">
    			<AppenderRef ref="le" />
    		</Root>
    	</Loggers>
    </Configuration>

The extra parameters are the following,

	<useDataHub>false</useDataHub>: Sent to a DataHub instance if true.
	<dataHubAddr>localhost</dataHubAddr>: The IP of the DataHub instance that we will connect to.
	<dataHubPort>10000</dataHubPort>: The Port of the DataHub instance that we will connect to.
	<logHostName>true</logHostName>: Prefixes log messages with a HostName
	<hostName>my_host</hostName>: The HostName to prefix each log message with. If not set will be automatically detected.
	<logId>log_id</logId>: The LogID to be prefixed with each log message. If not set it will not be logged.


Logging Messages
----------------

With that done, you are ready to send logs to Logentries.

In each class you wish to log from, enter the following using directives at the top if not already there:

	import org.apache.logging.log4j.Logger;
	import org.apache.logging.log4j.LogManager;

Then create this object at class-level:

    private static Logger log = LogManager.getRootLogger();

Now within your code in that class, you can log using log4j2 as normal and it will log to Logentries.

Example:

    log.debug("Debugging Message");
    log.info("Informational message");
    log.warn("Warning Message");


LOGBACK
=======

To configure logback, you will need to perform the following:

    * (1) Install Logback (if you are not already using it).
    * (2) Install the Logentries Logback plugin.
    * (3) Configure the Logentries Logback plugin.

Maven Users
-----------

Place this in your pom.xml

    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.5</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.11</version>
        </dependency>
        <dependency>
            <groupId>com.logentries</groupId>
            <artifactId>logentries-appender</artifactId>
            <version>RELEASE</version>
        </dependency>
    </dependencies>

Configure the logback plugin
----------------------------

Download the required logback.xml config file from <a href="https://github.com/logentries/le_java/raw/master/configFiles/logback.xml.example">here</a>

Add this file to your project as it is the config which adds the plugin for logback to send logs to Logentries. This file should be in added to the classpath.

In this file, you will see the following:

    <?xml version="1.0" encoding="UTF-8" ?>
    <configuration>

        <appender name="LE"
            class="com.logentries.logback.LogentriesAppender">
            <Token>LOGENTRIES_TOKEN</Token>
            <Ssl>False</Ssl>
            <facility>USER</facility>
            <layout>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </layout>
        </appender>

        <root level="debug">
            <appender-ref ref="LE" />
        </root>
    </configuration>

Replace the value "LOGENTRIES_TOKEN" with the token UUID that is to the right of your newly created logfile.

Note that internal debug support for the appender itself is only available with log4j.

Logging to DataHub
----------------

To log to a DataHub we can change logback.xml configuration to send logs to your instance of DataHub as seeb below.

    <?xml version="1.0" encoding="UTF-8" ?>
    <configuration>

        <appender name="LE"
            class="com.logentries.logback.LogentriesAppender">
            <Token>LOGENTRIES_TOKEN</Token>
            <Ssl>False</Ssl>
            <IsUsingDataHub>True</IsUsingDataHub>
            <DataHubAddr>localhost</DataHubAddr>
            <DataHubPort>10000</DataHubPort>
            <LogHostName>true</LogHostName>
            <LogID>MyLog</LogID>
            <facility>USER</facility>
            <layout>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </layout>
        </appender>

        <root level="debug">
            <appender-ref ref="LE" />
        </root>
    </configuration>

The extra parameters are the following,
```
 <IsUsingDataHub>True</IsUsingDataHub>: Sent to a DataHub instance if true.
 <DataHubAddr>localhost</DataHubAddr>: The IP of the DataHub instance that we will connect to.
 <DataHubPort>10000</DataHubPort>: The Port of the DataHub instance that we will connect to.
 <LogHostName>true</LogHostName>: Prefixes log messages with a HostName
 <HostName>MyHost</HostName>: The HostName to prefix each log message with. If not set will be automatically detected.
 <LogID>MyLog</LogID>: The LogID to be prefixed with each log message. If not set it will not be logged.
```
Logging Messages
----------------

With that done, you are ready to send logs to Logentries.

In each class you wish to log from, enter the following using directives at the top if not already there:

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

Then create this object at class-level:

    private static Logger log = LoggerFactory.getLogger("logentries");

Now within your code in that class, you can log using logback as normal and it will log to Logentries.

Example:

    log.debug("Debugging Message");
    log.info("Informational message");
    log.warn("Warning Message");


Maximum Log Length
==================

Currently logs which exceed 65536 characters in length, including any patterns and timestamps you may include, will be split and sent as multiple logs.
