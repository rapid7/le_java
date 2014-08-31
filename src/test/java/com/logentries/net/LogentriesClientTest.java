package com.logentries.net;

import org.junit.Test;
import static org.junit.Assert.*;

public class LogentriesClientTest {

	private static final String API_HTTP_SERVER = "api.logentries.com";
	private static final String API_TOKEN_SERVER = "data.logentries.com";
	private static final String DATAHUB_IP = "127.0.0.1";
	private static final int LE_SSL_PORT = 443;
	private static final int LE_PORT = 80;
	private static final int DATAHUB_PORT = 10000;

	@Test
	public void testGetAddress()
	{
		// HttpPut = true,  SSL = false
		LogentriesClient client = new LogentriesClient(true, false, false, "", 0);
		assertEquals("api.logentries.com should be used for HTTP PUT", client.getAddress(), API_HTTP_SERVER);

		// HttpPut = false, SSL = false
		LogentriesClient client2 = new LogentriesClient(false, false, false, "", 0);
		assertEquals("data.logentries.com should be used for Token TCP", client2.getAddress(), API_TOKEN_SERVER);
	}

	@Test
	public void testGetPort()
	{
		// HttpPut = true, SSL = true
		LogentriesClient client3 = new LogentriesClient(true, true, false, "", 0);
		assertEquals("Port 443 should be used for SSL over HTTP", client3.getPort(), LE_SSL_PORT);

		// HttpPut = true, SSL = false
		LogentriesClient client4 = new LogentriesClient(true, false, false, "", 0);
		assertEquals("Port 80 should be used for HTTP PUT", client4.getPort(), LE_PORT);

		// HttpPut = false, SSL = true
		LogentriesClient client5 = new LogentriesClient(false, true, false, "", 0);
		assertEquals("Port 443 should be used for SSL over Token TCP", client5.getPort(), LE_SSL_PORT);

		// HttpPut = false, SSL = false
		LogentriesClient client6 = new LogentriesClient(false, false, false, "", 0);
		assertEquals("Port 80 should be used for Token TCP", client6.getPort(), LE_PORT);
	}

	@Test
	public void testDataHubAddress()
	{

		LogentriesClient client = new LogentriesClient(true, true, true, "127.0.0.1", 10000);
		assertEquals("Address 127.0.0.1 should be used over api.logentries.com", client.getAddress(), DATAHUB_IP);

		LogentriesClient client2 = new LogentriesClient(true, false, true, "127.0.0.1", 10000);
		assertEquals("Address 127.0.0.1 should be used over api.logentries.com", client2.getAddress(), DATAHUB_IP);

		LogentriesClient client3 = new LogentriesClient(false, true, true, "127.0.0.1", 10000);
		assertEquals("Address 127.0.0.1 should be used over data.logentries.com", client3.getAddress(), DATAHUB_IP);

		LogentriesClient client4 = new LogentriesClient(false, false, true, "127.0.0.1", 10000);
		assertEquals("Address 127.0.0.1 should be used over data.logentries.com", client4.getAddress(), DATAHUB_IP);

		LogentriesClient client5 = new LogentriesClient(true, true, false, "127.0.0.1", 10000);
		assertNotEquals("Address api.logentries.com should be used over 127.0.0.1", client5.getAddress(), DATAHUB_IP);

		LogentriesClient client6 = new LogentriesClient(true, false, false, "127.0.0.1", 10000);
		assertNotEquals("Address api.logentries.com should be used over 127.0.0.1", client6.getAddress(), DATAHUB_IP);

		LogentriesClient client7 = new LogentriesClient(false, true, false, "127.0.0.1", 10000);
		assertNotEquals("Address data.logentries.com should be used over 127.0.0.1", client7.getAddress(), DATAHUB_IP);

		LogentriesClient client8 = new LogentriesClient(false, false, false, "127.0.0.1", 10000);
		assertNotEquals("Address data.logentries.com should be used over 127.0.0.1", client8.getAddress(), DATAHUB_IP);
	}


	@Test
	public void testDataHubPort()
	{

		LogentriesClient client = new LogentriesClient(true, true, true, "127.0.0.1", 10000);
		assertEquals("Port 10000 should be used over 443", client.getPort(), DATAHUB_PORT);

		LogentriesClient client2 = new LogentriesClient(false, false, true, "127.0.0.1", 10000);
		assertEquals("Port 10000 should be used over 80", client2.getPort(), DATAHUB_PORT);

		LogentriesClient client3 = new LogentriesClient(false, false, false, "127.0.0.1", 10000);
		assertNotEquals("Port 80 should be used over DataHubs port", client3.getPort(), DATAHUB_PORT);

		LogentriesClient client4 = new LogentriesClient(true, false, false, "127.0.0.1", 10000);
		assertNotEquals("Port 80 should be used over DataHubs port", client4.getPort(), DATAHUB_PORT);

		LogentriesClient client5 = new LogentriesClient(false, true, false, "127.0.0.1", 10000);
		assertNotEquals("Port 443 should be used over DataHubs port", client5.getPort(), DATAHUB_PORT);

		LogentriesClient client6 = new LogentriesClient(true, true, false, "127.0.0.1", 10000);
		assertNotEquals("Port 443 should be used over DataHubs port", client6.getPort(), DATAHUB_PORT);
	}
}
