Logging To Logentries from Java
==============================

Logentries currently supports logging from Java using the following logging libraries:

* [Log4J](https://github.com/logentries/le_java#log4j)
* [Logback](https://github.com/logentries/le_java#logback)

--------------------------------------------------------------

Account Setup
-------------
You can sign up for a Logentries account simply by clicking Sign Up and entering your email address. Once you have your credentials and have logged in,
create a new host in the UI with a name that represents your app. Then, select this host and create a new logfile with a name that represents what you're
logging. Select 'TOKEN TCP' as the source_type and click Register to create the log.

--------------------------------------------------------------

LOG4J
========

To configure log4j, you will need to perform the following:

    * (1) Install Log4j (if you are not already using it).
    * (2) Install the Logentries Log4j plugin.
    * (3) Configure the Logentries Log4j plugin.

Maven Users
-----------

Place this in your pom.xml

	<repositories>
	    <repository>
	        <id>logentries-releases</id>
	        <url>https://github.com/logentries/logentries-mvn-repo/raw/master/releases</url>
	    </repository>
	</repositories>
	<dependencies>
		<dependency>
	    	<groupId>log4j</groupId>
	    	<artifactId>log4j</artifactId>
	    	<version>1.2.17</version>
		</dependency>
	    <dependency>
	        <groupId>com.logentries</groupId>
	        <artifactId>logentries-appender</artifactId>
	        <version>1.1.8</version>
	    </dependency>
	</dependencies>

Manual Install
--------------

Download log4j from:

https://logging.apache.org/log4j/1.2/download.html

Retrieve log4j jar file and place it the `WEB-INF/lib` folder of your project.

Then add it to the build path from within your project.

The next file you need is logentriesappender-1.1.8.jar which is the plugin for log4j. You can get it <a href="https://github.com/logentries/logentries-mvn-repo/raw/master/releases/com/logentries/logentries-appender/1.1.8/logentries-appender-1.1.8.jar">here.</a>

Place this in the `WEB-INF/lib` folder of your project and add it to the buildpath as done above with log4j jar.

Configure the Log4J plugin
-------------------------- 

Download the required log4j.xml config file from here

Add this file to your project as it is the config which adds the plugin for log4j to send logs to Logentries. This file should be in added to the classpath.

In this file, you will see the following:

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
	<log4j:configuration debug="true">
	<appender name="le" class="com.logentries.log4j.LogentriesAppender">
		<!-- Enter your Logentries token, like bc0c4f90-a2d6-11e1-b3dd-0800200c9a66 -->
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

Replace the value "LOGENTRIES_TOKEN" with the token UUID that is to the right of your newly created logfile.
    
For debugging purposes set the debug parameter to true.

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


LOGBACK
==========

To configure logback, you will need to perform the following:

    * (1) Install Logback (if you are not already using it).
    * (2) Install the Logentries Logback plugin.
    * (3) Configure the Logentries Logback plugin.

Maven Users
-----------

Place this in your pom.xml

	<repositories>
	    <repository>
	        <id>logentries-releases</id>
	        <url>https://github.com/logentries/logentries-mvn-repo/raw/master/releases</url>
	    </repository>
	</repositories>
	<dependencies>
		<dependency>
    		<groupId>ch.qos.logback</groupId>
    		<artifactId>logback-classic</artifactId>
    		<version>0.9.30</version>
		</dependency>
	    <dependency>
	        <groupId>com.logentries</groupId>
	        <artifactId>logentries-appender</artifactId>
	        <version>1.1.8</version>
	    </dependency>
	</dependencies>