Logging To Logentries from Java Platform
==========================================================

Logentries currently supports Log4J logging from Java

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

To obtain your Logentries account key you must download the getKey exe from github at:

    T.B.D
    
This user-key is essentially a password to your account and is required for each of the steps listed below. To get the key unzip the file you download and run the following from the command line:

    getKey.exe --key

You will be required to provide your user name and password here. Save the key as you will need this later on. 

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

The second file required is called log4j.properties and is available on github at:

https://github.com/downloads/logentries/le_java/log4j.properties

Add this file to your project as it is the config which adds the plugin for log4J to send logs to Logentries,

or create it yourself if you wish. To do so, simple create a a properties file, log4j.properties and add the 

following to it:

	log4j.rootLogger=DEBUG, LE
	log4j.appender.LE=com.logentries.log4j.LeAppender
	log4j.appender.LE.Key=YOUR-USER-KEY-HERE
	log4j.appender.LE.Location=YOUR-LOG-DESTINATION-HERE
	log4j.appender.LE.layout=org.apache.log4j.PatternLayout
	log4j.appender.LE.layout.ConversionPattern=%d{EEE MMM dd HH:mm:ss ZZZ yyyy}, (%F:%L) %-5p: %m

In this file you will need to enter your user-key as obtained above with the getKey script in the required
Key value.

You must also include in the required Destination value the name of your host and logfile on Logentries

in the following format:        `hostname/logname.log`


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

