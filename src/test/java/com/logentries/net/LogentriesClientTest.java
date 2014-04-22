package com.logentries.net;

import org.junit.Test;
import static org.junit.Assert.*;

public class LogentriesClientTest {

	private static final String API_HTTP_SERVER = "api.logentries.com";
	private static final String API_TOKEN_SERVER = "data.logentries.com";
	private static final int LE_SSL_PORT = 443;
	private static final int LE_PORT = 80;

	@Test
	public void testGetAddress()
	{
		// HttpPut = true,  SSL = false
		LogentriesClient client = new LogentriesClient(true, false);
		assertEquals("api.logentries.com should be used for HTTP PUT", client.getAddress(), API_HTTP_SERVER); 

		// HttpPut = false, SSL = false
		LogentriesClient client2 = new LogentriesClient(false, false);
		assertEquals("data.logentries.com should be used for Token TCP", client2.getAddress(), API_TOKEN_SERVER);
	}

	@Test
	public void testGetPort()
	{
		// HttpPut = true, SSL = true
		LogentriesClient client3 = new LogentriesClient(true, true);
		assertEquals("Port 443 should be used for SSL over HTTP", client3.getPort(), LE_SSL_PORT);

		// HttpPut = true, SSL = false
		LogentriesClient client4 = new LogentriesClient(true, false);
		assertEquals("Port 80 should be used for HTTP PUT", client4.getPort(), LE_PORT);

		// HttpPut = false, SSL = true
		LogentriesClient client5 = new LogentriesClient(false, true);
		assertEquals("Port 443 should be used for SSL over Token TCP", client5.getPort(), LE_SSL_PORT);

		// HttpPut = false, SSL = false
		LogentriesClient client6 = new LogentriesClient(false, false);
		assertEquals("Port 80 should be used for Token TCP", client6.getPort(), LE_PORT);
	}
}
