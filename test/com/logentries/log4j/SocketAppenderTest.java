package com.logentries.log4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import com.logentries.log4j.LeAppender.SocketAppender;

/**
 * Tests the Logentries log4j component.
 * 
 * @author Viliam Holub
 * 
 */
public class SocketAppenderTest {

	static final UUID u0 = UUID.randomUUID();
	static final UUID u1 = UUID.randomUUID();

	class Receiver extends Thread {

		final ServerSocket server;
		boolean accepted = false;
		int read_data = 0;
		byte[] data = new byte[ 1024];

		Receiver( int port) throws IOException, InterruptedException {
			Thread.sleep( 100);
			server = new ServerSocket( port);
			server.setReuseAddress( true);
		}

		@Override
		public void run() {
			OutputStream out = null;
			InputStream in = null;
			try {
				Socket socket = server.accept();
				accepted = true;
				out = socket.getOutputStream();
				in = socket.getInputStream();
				while (read_data != -1 && !interrupted()) {
					read_data = in.read( data);
					
					//XXX
					if (read_data >= 0)
						System.out.println( new String( Arrays.copyOf( data, read_data)));
				}
			} catch (IOException e) {
				return;
			}
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	@Test
	public void testSocketAppender() {
		LeAppender l = new LeAppender( true);
		SocketAppender a = l.new SocketAppender();
		assertTrue( a.isDaemon());
		assertFalse( a.isAlive());
		l.close();
	}

	@Test
	public void testReopenConnection() throws InterruptedException, IOException {
		Receiver r = new Receiver( LeAppender.LE_LOCAL_PORT);
		r.start();

		LeAppender l = new LeAppender( true);
		l.setKey( u0.toString());
		l.setLocation( u1.toString());
		SocketAppender a = l.new SocketAppender();

		a.reopenConnection();
		Thread.sleep( 100);
		assertTrue( r.accepted);
		assertNotNull( a.socket);
		assertNotNull( a.stream);
		byte[] expected = String.format(
				"PUT /%s/hosts/%s/?realtime=1 HTTP/1.1\r\n\r\n", u0, u1)
				.getBytes();
		assertEquals( expected.length, r.read_data);
		assertEquals( new String( expected),
				new String( Arrays.copyOf( r.data, r.read_data)));

		l.close();
		r.server.close();
		r.interrupt();
		a.interrupt();
	}

	@Test
	public void testCloseConnection() throws IOException, InterruptedException {
		Thread.sleep( 100);
		Receiver r = new Receiver( LeAppender.LE_LOCAL_PORT);
		r.start();

		LeAppender l = new LeAppender( true);
		l.setKey( u0.toString());
		l.setLocation( u1.toString());
		SocketAppender a = l.new SocketAppender();

		a.reopenConnection();
		Thread.sleep( 100);
		assertTrue( r.accepted);
		assertTrue( -1 != r.read_data);
		assertNotNull( a.socket);
		assertNotNull( a.stream);

		a.closeConnection();
		assertNull( a.socket);
		assertNull( a.stream);
		Thread.sleep( 100);
		assertEquals( -1, r.read_data);

		l.close();
		r.server.close();
		r.interrupt();
		a.interrupt();
	}

	@Test
	public void testRun() throws InterruptedException, IOException {
		Thread.sleep( 100);
		Receiver r = new Receiver( LeAppender.LE_LOCAL_PORT);
		r.start();

		LeAppender l = new LeAppender( true);
		SocketAppender a = l.new SocketAppender();
		a.start();

		// Connection should be established
		Thread.sleep( 100);
		assertTrue( r.accepted);

		// Send data
		l.appendLine( "line");

		// The line should be read
		Thread.sleep( 100);
		assertEquals( "line\n", new String( Arrays.copyOf( r.data, r.read_data)));

		// Interrupt the sender
		a.interrupt();

		l.close();
		r.server.close();
		r.interrupt();
	}

}
