Logging To Logentries from Java
==============================

Logentries currently supports log4j logging from Java as well as PaaS, such as CloudBees

--------------------------------------------------------------

Simple Usage Example
--------------------

	public class HelloLoggingWorld {
	
		private static Logger log = LogManager.getRootLogger();
		
		public static void main(String[] args) throws IOException
		{
			log.warn("Warning log sent from java class");
		}
	}


--------------------------------------------------------------

To configure log4j, you will need to perform the following:

    * (1) Obtain your Logentries account key.
    * (2) Setup Log4j (if you are not already using it).
    * (3) Configure the Logentries Log4j plugin.

You can obtain your Logentries account key on the Logentries UI, by clicking account in the top left cornercand then display account key on the right.


log4j Setup
-----------

If you don't already have log4j set up in your project, please follow these steps:

Download log4j from:

https://logging.apache.org/log4j/1.2/download.html

Retrieve log4j jar file and place it the bin folder of your project.

Then add a reference to this jar from within your project.

Logentries log4j Plugin Setup
-----------------------------

The first file you need is logentries-VERSION.jar which is the plugin for log4j. It is available from github at:

https://github.com/logentries/le_java/downloads

Place this in the libs folder of your project and add it as a reference as done above with log4j jar.

The second file required is called log4j.xml and is available again on github on projects pages.

Add this file to your project as it is the config which adds the plugin for log4j to send logs to Logentries. This file needs to be in class path.

In this file, you will see the following:

	<log4j:configuration debug="true">
 	  <appender name="le" class="com.logentries.log4j.LeAppender">
   	    <param name="Key" value="LOGENTRIES_ACCOUNT_KEY"/>
        <param name="Location" value="LOGENTRIES_LOCATION"/>
        <param name="Debug" value="false"/>
		<param name="SSL" value="false" />
        <layout class="org.apache.log4j.PatternLayout">
          <param name="ConversionPattern" value="%d{EEE MMM dd HH:mm:ss ZZZ yyyy},  (%F:%L) %-5p: %m"/>
        </layout>
      </appender>
      <logger name="example">
        <level value="debug"/>
      </logger>
      <root>
        <priority value="debug"></priority>
        <appender-ref ref="le"/>
      </root>
    </log4j:configuration>

Replace the value "LOGENTRIES_ACCOUNT_KEY" with your account-key obtained earlier. Also replace the "LOGENTRIES_LOCATION" value. This should be in the following format:

    `hostname/logname.log`
    
For debugging purposes set the debug parameter to true. The appender will display debug information on console. You can also activate SSL encryption when used in public networks. Note that SSL encryption may be expensive in terms of CPU usage.


CloudBees
=========

To use this plugin on CloudBees, please follow all the above instructions but be sure to place both

log4j.jar and logentries.jar in the lib folder of your app. Also place log4j.xml in `/WEB_INF/classes/`

Those are the only difference to use it on CloudBees, below show's how to create the logger inside you classes.


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

