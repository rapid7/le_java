package com.logentries.logback;

import com.logentries.net.AsyncLogger;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LogentriesAppenderTest {

    private static final String token = "some-token";
    private static final String location = "some location";
    private static final String accountKey = "account key";
    private AsyncLogger client;
    private LogentriesAppender le;

    @Before
    public void setUp() {
        client = Mockito.mock(AsyncLogger.class);
        le = new LogentriesAppender(client);
    }

    @Test
    public void setterTests() {
        boolean doPut = true;
        boolean useSSL = true;

        le.setHttpPut(doPut);
        le.setToken(token);
        le.setLocation(location);
        le.setKey(accountKey);
        le.setSsl(useSSL);

        Mockito.verify(client).setHttpPut(doPut);
        Mockito.verify(client).setToken(token);
        Mockito.verify(client).setLocation(location);
        Mockito.verify(client).setKey(accountKey);
        Mockito.verify(client).setSsl(useSSL);
    }

    @Test
    public void testStart() {
        try {
            le.start();
        } catch (Throwable t) {
            Assert.fail("Shouldn't throw exception on startup!");
            return;
        }
        Assert.assertTrue("No exception thrown", true);
    }

    @Test
    public void testStop() {
        try {
            le.stop();
        } catch (Throwable t) {
            Assert.fail("Shouldn't throw exception on shutdown!");
            return;
        }
        Assert.assertTrue("No exception thrown", true);
    }
}
