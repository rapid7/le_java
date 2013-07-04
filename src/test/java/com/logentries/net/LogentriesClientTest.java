package com.logentries.net;

import org.junit.Test;
import static org.junit.Assert.*;

public class LogentriesClientTest {

	private static final String API_HTTP_SERVER = "api.logentries.com";
	private static final String API_TOKEN_SERVER = "api.logentries.com";
	private static final int HTTP_SSL_PORT = 443;
	private static final int HTTP_PORT = 80;
	private static final int TOKEN_PORT = 10000;
	private static final int TOKEN_TLS_PORT = 20000;

	@Test
	public void testGetAddress()
	{
		// HttpPut = true,  SSL = false
		LogentriesClient client = new LogentriesClient(true,false);
		assertEquals("api.logentries.com should be used for HTTP PUT", client.getAddress(), API_HTTP_SERVER); 

		// HttpPut = false, SSL = false
		LogentriesClient client2 = new LogentriesClient(false,false);
		assertEquals("data.logentries.com should be used for Token TCP", client2.getAddress(), API_TOKEN_SERVER);
	}

	@Test
	public void testGetPort()
	{
		// HttpPut = true, SSL = true
		LogentriesClient client3 = new LogentriesClient(true, true);
		assertEquals("Port 443 should be used for SSL over HTTP", client3.getPort(), HTTP_SSL_PORT);

		// HttpPut = true, SSL = false
		LogentriesClient client4 = new LogentriesClient(true, false);
		assertEquals("Port 80 should be used for HTTP PUT", client4.getPort(), HTTP_PORT);

		// HttpPut = false, SSL = true
		LogentriesClient client5 = new LogentriesClient(false, true);
		assertEquals("Port 20000 should be used for TLS over Token TCP", client5.getPort(), TOKEN_TLS_PORT);

		// HttpPut = false, SSL = false
		LogentriesClient client6 = new LogentriesClient(false, false);
		assertEquals("Port 10000 should be used for Token TCP", client6.getPort(), TOKEN_PORT);
	}
}
