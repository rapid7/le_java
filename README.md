Logging To Logentries from Java Platform
==========================================================

Logentries currently supports Log4J logging from Java as well as PaaS, such as CloudBees

--------------------------------------------------------------

Simple Usage Example
--------------------------------------------------------------

	public class HelloLoggingWorld {
	
		private static Logger log = LogManager.getRootLogger();
		
		public static void main(String[] args) throws IOException
		{
			log.warn("Warning log sent from java class");
		}
	}


--------------------------------------------------------------

To configure Log4J, you will need to perform the following:

    * (1) Obtain your Logentries account key.
    * (2) Setup Log4J (if you are not already using it).
    * (3) Configure the Logentries Log4NJ plugin.

You can obtain your Logentries account key on the Logentries UI, by clicking account in the top left corner

and then display account key on the right.

Log4J Setup
------------------

If you don't already have log4J set up in your project, please follow these steps:

Download log4J from:

http://www.apache.org/dyn/closer.cgi/logging/log4j/1.2.16/apache-log4j-1.2.16.zip

Retreive log4J.jar and place it the bin folder of your project.

Then add a reference to this jar from within your project. This is done simply by right-clicking

References, Click Add Reference and locate the jar in your project bin folder.

Logentries log4J Plugin Setup
--------------------------------

The first file you need is logentries.jar which is the plugin for log4j. It is available from github at:

https://github.com/downloads/logentries/le_java/logentries.jar

Place this in the bin folder of your project and add it as a reference as done above with log4j.jar.

The second file required is called log4j.xml and is available on github at:

https://github.com/downloads/logentries/le_java/log4j.xml

Add this file to your project as it is the config which adds the plugin for log4J to send logs to Logentries.

In this file, you will see the following:

	<log4j:configuration debug="true">
 	  <appender name="le" class="com.logentries.log4j.LeAppender">
   	    <param name="Key" value="LOGENTRIES_ACCOUNT_KEY"/>
            <param name="Location" value="LOGENTRIES_LOCATION"/>
            <param name="Debug" value="true"/>
            <layout class="org.apache.log4j.PatternLayout">
              <param name="ConversionPattern" value="%d{EEE MMM dd HH:mm:ss ZZZ yyyy},
                 (%F:%L) %-5p: %m"/>
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

Replace the value "LOGENTRIES_ACCOUNT_KEY" with your account-key obtained earlier. Also replace the "LOGENTRIES_LOCATION" value. The value you provide here will appear in your Logentries account and will be used to identify your machine and log events. This should be in the following format:

        `hostname/logname.log`


CloudBees
========================================

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

Now within your code in that class, you can log using log4J as normal and it will log to Logentries.

Example:

	log.Debug("Debugging Message");
	log.Info("Informational message");
	log.Warn("Warning Message");

