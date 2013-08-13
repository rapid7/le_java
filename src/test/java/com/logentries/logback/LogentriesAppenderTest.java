package com.logentries.logback;

import ch.qos.logback.classic.LoggerContext;
import com.logentries.net.AsyncLogger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class LogentriesAppenderTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(LogentriesAppenderTest.class);
	
	private static final String token = "some-token";
	private static final String location = "some location";
	private static final String accountKey = "account key";
	private static final String facility = "DAEMON";

    public  static LogentriesAppender newAppender(){
        LogentriesAppender le = new LogentriesAppender();
        le.setHttpPut(true);
        le.setToken(token);
        le.setLocation(location);
        le.setKey(accountKey);
        le.setSsl(true);
        le.setFacility(facility);
        return le;
    }


	
	@Test
	public void setterTests() {
		LogentriesAppender le = newAppender();
		assertEquals(le.le_async.getToken(),token);
		assertEquals(le.le_async.getHttpPut(),true);
		assertEquals(le.le_async.getKey(),accountKey);
		assertEquals(le.le_async.getLocation(),location);
		assertEquals(le.le_async.getSsl(),true);		 
	}


    public void correctStackTraceTest() throws Exception{
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(LogentriesAppenderTest.class);
//        LogentriesAppender appender = newAppender();
        LogentriesAppender appender = new LogentriesAppender();
        //appender.setToken("7aaa");
        appender.setFacility("USER");
        AsyncLogger asyncLogger = appender.le_async;
        logger.addAppender(appender);
        appender.start();
        asyncLogger.addLineToQueue("Only to initialize the client");
        Exception exception = new Exception("This is a test",new NullPointerException("Something is null"));
        log.error("An error has occurred for {} " , "no idea",exception );
        Thread.sleep(1500);

        System.out.println("Error");
    }
}

