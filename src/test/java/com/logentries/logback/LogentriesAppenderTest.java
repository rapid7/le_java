package com.logentries.logback;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogentriesAppenderTest {

	private static final Logger log = LoggerFactory
			.getLogger(LogentriesAppenderTest.class);
	private static final String token = "some-token";
	private static final String location = "some location";
	private static final String accountKey = "account key";
	private static final String facility = "DAEMON";

	@Test
	public void setterTests() {
//		LogentriesAppender le = new LogentriesAppender();
//		le.setHttpPut(true);
//		le.setToken(token);
//		le.setLocation(location);
//		le.setKey(accountKey);
//		le.setSsl(true);
//		le.setFacility(facility);
//		assertEquals(le.le_async.getToken(), token);
//		assertEquals(le.le_async.getHttpPut(), true);
//		assertEquals(le.le_async.getKey(), accountKey);
//		assertEquals(le.le_async.getLocation(), location);
//		assertEquals(le.le_async.getSsl(), true);
	}
}
