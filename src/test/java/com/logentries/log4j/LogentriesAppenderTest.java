package com.logentries.log4j;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogentriesAppenderTest {

	private static final String token = "some-token";
	private static final String location = "some location";
	private static final String accountKey = "account key";
	private static final String facility = "DAEMON";

	@Test
	public void settersTest() {
		LogentriesAppender le = new LogentriesAppender();
		le.setHttpPut(true);
		le.setToken(token);
		le.setLocation(location);
		le.setKey(accountKey);
		le.setSsl(true);
		assertEquals(le.le_async.getToken(),token);
		assertEquals(le.le_async.getHttpPut(),true);
		assertEquals(le.le_async.getKey(),accountKey);
		assertEquals(le.le_async.getLocation(),location);
		assertEquals(le.le_async.getSsl(),true);
	}

}
