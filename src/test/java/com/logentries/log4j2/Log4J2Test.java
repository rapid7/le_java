package com.logentries.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the Log4J2 appender.
 * Created by josh on 11/15/14.
 */
public class Log4J2Test
{
    private LoggerContext loggerContext;
    
    @Before
    public void setUp() {
        loggerContext = Configurator.initialize("test-log4j2", "log4j2-appender-test.xml");
    }
    
    @After
    public void tearDown() {
        Configurator.shutdown(loggerContext);
    }
    
    @Test
    public void testLog4J2Appender() throws Exception
    {
        Logger log = LogManager.getLogger("TEST-LOGGER");
        log.info("Hello there.");
        try
        {
            throw new Exception("This is an example exception.");
        }
        catch (Exception e)
        {
            log.error("This is an error, with an exception: " + e, e);
        }
    }
    
    @Test
    public void testLog4J2WithAsyncLogger() throws Exception {
        Logger log = LogManager.getLogger("test-async-logger");
        log.info("Test log line.");
    }
}
