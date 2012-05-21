package com.logentries.log4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Logentries appender for log4j.
 * 
 * @author Viliam Holub
 * @author Mark Lacomber
 * 
 */
public class LeAppender extends AppenderSkeleton {

	/*
	 * Constants
	 */

	/** Size of the internal event queue. */
	static final int QUEUE_SIZE = 32768;
	/** Logentries API server address. */
	static final String LE_API = "api.logentries.com";
	/** Logentries local API server address for testing. */
	static final String LE_LOCAL_API = "localhost";
	/** Default port number for Logentries API server. */
	static final int LE_PORT = 80;
	/** Default SSL port number for Logentries API server. */
	static final int LE_SSL_PORT = 443;
	/** Local port number for testing. */
	static final int LE_LOCAL_PORT = 8088;
	/** UTF-8 output character set. */
	static final Charset UTF8 = Charset.forName( "UTF-8");
	/** ASCII character set used by HTTP. */
	static final Charset ASCII = Charset.forName( "US-ASCII");
	/** Minimal delay between attempts to reconnect in milliseconds. */
	static final int MIN_DELAY = 100;
	/** Maximal delay between attempts to reconnect in milliseconds. */
	static final int MAX_DELAY = 10000;

	/*
	 * Fields
	 */

	/** factory for SSL connections. */
	final SSLSocketFactory ssl_factory;
	/** Account key. */
	String key;
	/** Log location. */
	String location;
	/** Use SSL indicator. */
	boolean ssl;
	/** Debug flag. */
	boolean debug;
	/** Make local connection only. */
	boolean local;
	// /** Indicator if the socket appender has been started. */
	boolean started;

	/** Asynchronous socket appender. */
	SocketAppender appender;
	/** Message queue. */
	ArrayBlockingQueue<byte[]> queue;

	/*
	 * Internal classes
	 */

	/**
	 * Asynchronous over the socket appender.
	 * 
	 * @author Viliam Holub
	 * 
	 */
	class SocketAppender extends Thread {
		/** Socket connection. */
		Socket socket;
		/** Output log stream. */
		OutputStream stream;

		/**
		 * Initializes the socket appender.
		 */
		SocketAppender() {
			super( "Logentries log4j appender");
			// Don't block shut down
			setDaemon( true);
		}

		/**
		 * Opens the TCP connection to Logentries.
		 * 
		 * @param key
		 * @param location
		 * @throws InterruptedException
		 * @throws IOException
		 */
		void reopenConnection() throws InterruptedException {
			// Close the previous connection
			closeConnection();

			// Try to open the connection until we get through
			int delay = MIN_DELAY;
			while (true) {
				try {
					final String api_addr = local ? LE_LOCAL_API : LE_API;
					final int port = local ? LE_LOCAL_PORT : LE_PORT;

					// Open physical connection
					if (ssl) {
						SSLSocket s = (SSLSocket) ssl_factory.createSocket(
								api_addr, LE_SSL_PORT);
						s.setTcpNoDelay( true);
						s.startHandshake();
						socket = s;
					} else {
						socket = new Socket( api_addr, port);

					}
					stream = socket.getOutputStream();

					// Send identification
					String header = String.format(
							"PUT /%s/hosts/%s/?realtime=1 HTTP/1.1\r\n\r\n",
							getKey(), getLocation());
					stream.write( header.getBytes( ASCII));
					
					break;
				} catch (IOException e) {
					// Get information if in debug mode
					if (getDebug()) {
						System.err.println( "Unable to connect to Logentries");
						e.printStackTrace();
					}

					// Wait between connection attempts
					delay *= 2;
					if (delay > MAX_DELAY)
						delay = MAX_DELAY;
					Thread.sleep( delay);
				}
			}
		}

		/**
		 * Closes the connection. Ignores errors.
		 */
		void closeConnection() {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// Nothing we can do here
				}
			}
			stream = null;
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// Nothing we can do here
				}
			}
			socket = null;
		}

		/**
		 * Initializes the connection and starts to log.
		 * 
		 */
		@Override
		public void run() {
			try {
				// Open connection
				reopenConnection();

				// Send data in queue
				while (true) {
					// Take data from queue
					byte[] data = queue.take();

					// Send data, reconnect if needed
					while (true) {
						try {
							stream.write( data);
							stream.flush();
						} catch (IOException e) {
							// Reopen the lost connection
							reopenConnection();
							continue;
						}
						break;
					}
				}
			} catch (InterruptedException e) {
				// We got interrupted, stop
			}

			closeConnection();
		}
	}

	/**
	 * Initializes asynchronous logging.
	 * 
	 * @param local make local connection to API server for testing
	 */
	LeAppender( boolean local) {
		this.local = local;
		ssl_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		queue = new ArrayBlockingQueue<byte[]>( QUEUE_SIZE);

		appender = new SocketAppender();
	}

	/**
	 * Initializes asynchronous logging.
	 */
	public LeAppender() {
		this( false);
	}

	/**
	 * Checks that key and location are set.
	 */
	boolean checkCredentials() {
		return key != null && location != null;
	}

	/**
	 * Sets the account key.
	 * 
	 * @param key account key
	 */
	public void setKey( String key) {
		this.key = key;
	}

	/**
	 * Returns the account key.
	 * 
	 * @return account key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the location
	 * 
	 * @param location new location
	 */
	public void setLocation( String location) {
		this.location = location;
	}

	/**
	 * Returns current location.
	 * 
	 * @return current location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the debug flag. Appender in debug mode will print error messages on
	 * error console.
	 * 
	 * @param debug debug flag to set
	 */
	public void setDebug( boolean debug) {
		this.debug = debug;
	}

	/**
	 * Returns current debug flag.
	 * 
	 * @return true if debugging is enabled
	 */
	public boolean getDebug() {
		return debug;
	}

	/**
	 * Sets to use SSL for connection with API server.
	 * 
	 * @param ssl true to enable SSL encryption
	 */
	public void setSSL( boolean ssl) {
		this.ssl = ssl;
	}

	/**
	 * Returns current state of SSL connection settings.
	 * 
	 * @return true if SSL is set
	 */
	public boolean getSSL() {
		return ssl;
	}

	/**
	 * Appends the data to internal queue to be send over the network.
	 * 
	 * It does not block. If the queue is full, it removes latest event first to
	 * make space.
	 * 
	 * @param line line to append
	 */
	void appendLine( String line) {
		// Convert the line to byte data
		byte[] data = (line + '\n').getBytes( UTF8);

		// Try to append data to queue
		boolean is_full = !queue.offer( data);

		// If it's full, remove latest item and try again
		if (is_full) {
			queue.poll();
			queue.offer( data);
		}
	}

	/**
	 * Implements AppenderSkeleton Append method, handles time and format
	 * 
	 * @event event to log
	 */
	@Override
	protected void append( LoggingEvent event) {
		// Check that we have all parameters set and socket appender running
		if (!checkCredentials())
			return;
		if (!started) {
			appender.start();
			started = true;
		}

		// Append message content
		appendLine( layout.format( event));

		// Append stack trace if present
		final String[] stack = event.getThrowableStrRep();
		if (stack != null) {
			for (String line : stack)
				appendLine( line);
		}
	}

	/**
	 * Closes all connections to Logentries
	 */
	@Override
	public void close() {
		appender.interrupt();
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

}
