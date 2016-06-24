package com.logentries.net;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class AsyncLoggerTest {

	private static final String VALID_UUID = "a7ac14c3-2cc9-4f09-8fb3-73c5523e065c";

	@Test
	public void testGetAndSetToken()
	{
		AsyncLogger async = new AsyncLogger();
		assertEquals("token should be empty string by default", async.getToken(), "");
		async.setToken("randomToken");
		assertEquals("getToken should return correct token", async.getToken(), "randomToken");
	}

	@Test
    public void testOversizeMessage()
    {
        AsyncLogger async = new AsyncLogger();
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 2100000; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        async.addLineToQueue(output);
    }

    @Test
	public void testGetAndSetHttpPut()
	{
		AsyncLogger async = new AsyncLogger();
		assertFalse("httpput should be false by default", async.getHttpPut());
		async.setHttpPut(true);
		assertTrue("getHttpPut should have returned true", async.getHttpPut());
	}

	@Test
	public void testGetAndSetSsl()
	{
		AsyncLogger async = new AsyncLogger();
		assertFalse("ssl should be false by default", async.getSsl());
		async.setSsl(true);
		assertTrue("getSsl should have returned true", async.getSsl());
	}

	@Test
	public void testGetAndSetKey()
	{
		AsyncLogger async = new AsyncLogger();
		assertEquals("key should be empty string by default", async.getToken(), "");
		async.setToken("randomKey");
		assertEquals("getKey should return correct key", async.getToken(), "randomKey");
	}

	@Test
	public void testGetAndSetLocation()
	{
		AsyncLogger async = new AsyncLogger();
		assertEquals("locaton should be empty string by default", async.getToken(), "");
		async.setToken("randomLocation");
		assertEquals("getLocation should return correct location", async.getToken(), "randomLocation");
	}

	@Test
	public void testCheckCredentialsMissingToken()
	{
		AsyncLogger async = new AsyncLogger();
		assertFalse("checkCredentials should return false for empty token string", async.checkCredentials());
		async.setToken("LOGENTRIES_TOKEN");
		assertFalse("checkCredentials should return false for default token string", async.checkCredentials());
	}

	@Test
	public void testCheckCredentialsValidToken()
	{
		AsyncLogger async = new AsyncLogger();
		async.setToken("not-a-uuid");
		assertFalse("checkCredentials should return false for invalid token", async.checkCredentials());
		async.setToken(VALID_UUID);
		assertTrue("checkCredentials should return true for valid token", async.checkCredentials());
	}

	@Test
	public void testCheckCredentialsMissingKey()
	{
		AsyncLogger async = new AsyncLogger();
		async.setHttpPut(true);
		async.setLocation("anywhere");
		assertFalse("checkCredentials should return false for missing key", async.checkCredentials());
	}

	@Test
	public void testCheckCredentialsValidKey()
	{
		AsyncLogger async = new AsyncLogger();
		async.setHttpPut(true);
		async.setKey("not-a-uuid");
		async.setLocation("anywhere");
		assertFalse("checkCredentials should return false for invalid key", async.checkCredentials());
		async.setKey(VALID_UUID);
		assertTrue("checkCredenetials should return true for valid key and location", async.checkCredentials());
	}

	@Test
	public void testCheckCredentialsValidLocation()
	{
		AsyncLogger async = new AsyncLogger();
		async.setHttpPut(true);
		async.setKey(VALID_UUID);
		assertFalse("checkCredentials should return false for empty location", async.checkCredentials());
		async.setLocation("anywhere");
		assertTrue("checkCredentials should return true for valid location", async.checkCredentials());
	}

	@Test
	public void testCheckValidUUID()
	{
		AsyncLogger async = new AsyncLogger();
		assertFalse("checkValidUUID should return false for an empty string", async.checkValidUUID(""));
		assertFalse("checkValidUUID should return false for invalid uuid", async.checkValidUUID("not-a-uuid"));
		assertTrue("checkValidUUID should return true for valid uuid", async.checkValidUUID(VALID_UUID));
	}
}
