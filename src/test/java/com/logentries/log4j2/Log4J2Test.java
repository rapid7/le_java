package com.logentries.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

/**
 * Test the Log4J2 appender.
 * Created by josh on 11/15/14.
 */
public class Log4J2Test
{
    @Test
    public void testLog4J2Appender() throws Exception
    {
        Configurator.initialize("test-log4j2", "log4j2-appender-test.xml");
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
        Thread.sleep(2000);
    }
}
