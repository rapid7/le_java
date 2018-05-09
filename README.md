# Using InsightOps?
Please use our updated [libraries] which support specifying the region to send your log data to: 
[Log4j](https://insightops.help.rapid7.com/docs/log4j-log4j2), [Logback](https://insightops.help.rapid7.com/docs/logback), and [JavaUtil](https://insightops.help.rapid7.com/docs/java-util-logging)

[![Build Status](https://travis-ci.org/logentries/le_java.png?branch=master)](https://travis-ci.org/logentries/le_java)

[Please see our wiki for full documentation and installation](https://github.com/logentries/le_java/wiki)
-------

Logging To Logentries from Java
==============================

Logentries currently supports logging from Java using the following logging libraries:

* [Log4J](https://github.com/logentries/le_java/wiki/Log4j)
* [Log4J2](https://github.com/logentries/le_java/wiki/Log4j2)
* [Logback](https://github.com/logentries/le_java/wiki/Logback)
* [Java Util Logging](https://github.com/logentries/le_java/wiki/Java-Util-Logging)
[Please see our wiki for full documentation and installation](https://github.com/logentries/le_java/wiki)
-------

Account Setup
=============

You can sign up for a Logentries account [here](https://logentries.com/quick-start/). Once you have your credentials and have logged in,
create a new host in the UI with a name that represents your app. Then, select this host and create a new logfile with a name that represents what you're
logging. Select 'TOKEN TCP' as the source_type and click Register to create the log.

-------

Maximum Log Length
==================

Currently logs which exceed 65536 characters in length, including any patterns and timestamps you may include, will be split and sent as multiple logs.

-------

Contact Support
==================

Please email our support team at support@logentries.com if you need any help.

-------
