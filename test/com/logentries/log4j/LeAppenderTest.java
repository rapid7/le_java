package com.logentries.log4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Logentries log4j component.
 * 
 * @author Viliam Holub
 * 
 */
public class LeAppenderTest {

	/** General LE appender ready for tweaking. */
	LeAppender x;

	/** Some random key. */
	final static String k0 = UUID.randomUUID().toString();
	/** Some random key. */
	final static String k1 = UUID.randomUUID().toString();
	/* Some random location. */
	final static String l0 = "location0";
	/* Some random location. */
	final static String l1 = "location1";

	@Before
	public void setup() {
		x = new LeAppender( true);
	}

	@Test
	public void testLeAppender() {
		LeAppender l = new LeAppender();
		assertFalse( l.local);
		l.close();
	}

	@Test
	public void testLeAppenderBoolean() {
		LeAppender l = new LeAppender( true);

		assertTrue( l.local);
		assertFalse( l.debug);
		assertNotNull( l.appender);
		assertFalse( l.appender.isAlive());
		assertNotNull( l.ssl_factory);

		l.close();
	}

	@Test
	public void testSetKey() {
		x.setKey( k0);
		assertEquals( k0, x.key);
		x.setKey( k1);
		assertEquals( k1, x.key);
	}

	@Test
	public void testGetKey() {
		x.setKey( k0);
		assertEquals( k0, x.getKey());
		x.setKey( k1);
		assertEquals( k1, x.getKey());
	}

	@Test
	public void testSetLocation() {
		x.setLocation( l0);
		assertEquals( l0, x.location);

		x.setLocation( l1);
		assertEquals( l1, x.location);
	}

	@Test
	public void testGetLocation() {
		x.setLocation( l0);
		assertEquals( l0, x.getLocation());

		x.setLocation( l1);
		assertEquals( l1, x.getLocation());
	}

	@Test
	public void testSetDebug() {
		x.setDebug( true);
		assertTrue( x.debug);
		x.setDebug( false);
		assertFalse( x.debug);
	}

	@Test
	public void testGetDebug() {
		x.setDebug( true);
		assertTrue( x.getDebug());
		x.setDebug( false);
		assertFalse( x.getDebug());
	}

	@Test
	public void testSetSSL() {
		x.setSSL( true);
		assertTrue( x.ssl);
		x.setSSL( false);
		assertFalse( x.ssl);
	}

	@Test
	public void testGetSSL() {
		x.setSSL( true);
		assertTrue( x.getSSL());
		x.setSSL( false);
		assertFalse( x.getSSL());
	}

	@Test
	public void testCheckCredentials() throws InterruptedException {
		LeAppender l = new LeAppender( true);
		assertFalse( l.checkCredentials());
		l.setKey( k0);
		assertFalse( l.checkCredentials());
		l.setLocation( l0);
		assertTrue( l.checkCredentials());
		l.close();
	}

	@Test
	public void testAppendLine() {
		LeAppender l = new LeAppender( true);

		String line0 = "line0";
		l.appendLine( line0);
		assertEquals( 1, l.queue.size());

		for (int i = 0; i < LeAppender.QUEUE_SIZE + 100; i++)
			l.appendLine( "line" + i);
		assertEquals( LeAppender.QUEUE_SIZE, l.queue.size());

		l.close();
	}

	@Test
	public void testAppendLoggingEvent() {
		LeAppender l = new LeAppender( true);
		l.setLayout( new PatternLayout());

		LoggingEvent event = new LoggingEvent( "Critical",
				Logger.getRootLogger(), Level.DEBUG,
				System.currentTimeMillis(), (Throwable) null);
		l.append( event);
		assertEquals( 0, l.queue.size());

		l.setKey( k0);
		l.setLocation( l0);
		l.append( event);
		assertEquals( 1, l.queue.size());

		l.close();
	}

	@Test
	public void testClose() throws InterruptedException {
		LeAppender l = new LeAppender( true);
		l.setKey( k0);
		l.setLocation( l0);
		l.appender.start();

		// Wait until the appender is active
		for (int i = 0; i < 10; i++) {
			Thread.sleep( 100);
			if (l.appender.isAlive())
				break;
		}
		assertTrue( l.appender.isAlive());

		l.close();

		// Wait until the appender is not active
		for (int i = 0; i < 10; i++) {
			Thread.sleep( 100);
			if (!l.appender.isAlive())
				break;
		}
		assertFalse( l.appender.isAlive());
	}

	@Test
	public void testRequiresLayout() {
		assertTrue( x.requiresLayout());
	}

}
