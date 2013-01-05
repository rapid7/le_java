package com.logentries.log4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Logentries appender for log4j.
 * 
 * VERSION: 1.1.6
 * 
 * @author Viliam Holub
 * @author Mark Lacomber
 * 
 */
public class LogentriesAppender extends AppenderSkeleton {

	/*
	 * Constants
	 */

	/** Current Version number of library/ */
	static final String VERSION = "1.1.6";
	/** Size of the internal event queue. */
	static final int QUEUE_SIZE = 32768;
	/** Logentries API server address. */
	static final String LE_API = "api.logentries.com";
	/** Logentries local API server address for testing. */
	static final String LE_LOCAL_API = "localhost";
	/** Port number for Token logging on Logentries API server. */
	static final int LE_PORT = 10000;
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
	/** LE appender signature - used for debugging messages. */
	static final String LE = "LE ";
	/** Error message displayed when invalid API key is detected. */
	static final String INVALID_TOKEN = "\n\nIt appears your LOGENTRIES_TOKEN parameter in log4j.xml is incorrect!\n\n";
	/*
	 * Fields
	 */

	/** Destination Token. */
	String token;
	/** Debug flag. */
	boolean debug;
	/** Make local connection only. */
	boolean local;
	// /** Indicator if the socket appender has been started. */
	boolean started;

	/** Asynchronous socket appender. */
	SocketAppender appender;
	/** Message queue. */
	ArrayBlockingQueue<String> queue;

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
		/** Random number generator for delays between reconnection attempts. */
		final Random random = new Random();

		/**
		 * Initializes the socket appender.
		 */
		SocketAppender() {
			super( "Logentries log4j appender");
			// Don't block shut down
			setDaemon( true);
		}

		/**
		 * Opens connection to Logentries.
		 * 
		 * @throws IOException
		 */
		void openConnection() throws IOException {
			final String api_addr = local ? LE_LOCAL_API : LE_API;
			final int port = local ? LE_LOCAL_PORT : LE_PORT;

			dbg( "Reopening connection to Logentries API server " + api_addr
					+ ":" + port);

			// Open physical connection
			socket = new Socket( api_addr, port);
			
			stream = socket.getOutputStream();

			dbg( "Connection established");
		}

		/**
		 * Tries to opens connection to Logentries until it succeeds.
		 * 
		 * @throws InterruptedException
		 */
		void reopenConnection() throws InterruptedException {
			// Close the previous connection
			closeConnection();

			// Try to open the connection until we get through
			int root_delay = MIN_DELAY;
			while (true) {
				try {
					openConnection();

					// Success, leave
					return;
				} catch (IOException e) {
					// Get information if in debug mode
					if (debug) {
						dbg( "Unable to connect to Logentries");
						e.printStackTrace();
					}
				}

				// Wait between connection attempts
				root_delay *= 2;
				if (root_delay > MAX_DELAY)
					root_delay = MAX_DELAY;
				int wait_for = root_delay + random.nextInt( root_delay);
				dbg( "Waiting for " + wait_for + "ms");
				Thread.sleep( wait_for);
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
					String data = queue.take();

					// Replace newlines with line separator character to format multi-line events nicely in Logentries UI
					data = data.replace('\n', '\u2028');
					
					// Add newline to end the event
					data += '\n';
					
					// Get bytes of final event
					byte[] finalLine = data.getBytes(UTF8);
					
					// Send data, reconnect if needed
					while (true) {
						try {
							stream.write( finalLine);
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
				dbg( "Asynchronous socket writer interrupted");
			}

			closeConnection();
		}
	}

	/**
	 * Initializes asynchronous logging.
	 * 
	 * @param local make local connection to API server for testing
	 */
	LogentriesAppender( boolean local) {
		this.local = local;
		
		queue = new ArrayBlockingQueue<String>( QUEUE_SIZE);

		appender = new SocketAppender();
	}

	/**
	 * Initializes asynchronous logging.
	 */
	public LogentriesAppender() {
		this( false);
	}

	/**
	 * Checks that key and location are set.
	 */
	boolean checkCredentials() {
		if (token == null)
			return false;
		
		//Quick test to see if LOGENTRIES_TOKEN is a valid UUID
		UUID u = UUID.fromString(token);
		if (!u.toString().equals(token))
		{
			dbg(INVALID_TOKEN);
			return false;
		}
		
		return true;
	}

	/**
	 * Sets the token
	 * 
	 * @param token new token
	 */
	public void setToken( String token) {
		this.token = token;
		dbg( "Setting token to " + token);
	}

	/**
	 * Returns current token.
	 * 
	 * @return current token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the debug flag. Appender in debug mode will print error messages on
	 * error console.
	 * 
	 * @param debug debug flag to set
	 */
	public void setDebug( boolean debug) {
		this.debug = debug;
		dbg( "Setting debug to " + debug);
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
	 * Appends the data to internal queue to be send over the network.
	 * 
	 * It does not block. If the queue is full, it removes latest event first to
	 * make space.
	 * 
	 * @param line line to append
	 */
	void appendLine( String line) {
		dbg( "Queueing " + line);

		// Prefix the data with Token
		String data = token + line;

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
		if (!started && checkCredentials()) {
			dbg( "Starting Logentries asynchronous socket appender");
			appender.start();
			started = true;
		}

		// Render the event according to layout
		String formattedEvent = layout.format( event);

		// Append stack trace if present
		String[] stack = event.getThrowableStrRep();
		if (stack != null)
		{
			int len = stack.length;
			formattedEvent += ", ";
			for(int i = 0; i < len; i++)
			{
				formattedEvent += stack[i];
				if(i < len - 1)
					formattedEvent += "\u2028";
			}
		}
				
		// Prepare to be queued
		appendLine(formattedEvent);
	}

	/**
	 * Closes all connections to Logentries
	 */
	@Override
	public void close() {
		appender.interrupt();
		dbg( "Closing Logentries asynchronous socket appender");
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * Prints the message given. Used for internal debugging.
	 * 
	 * @param msg message to display
	 */
	void dbg( String msg) {
		if (debug)
			System.err.println( LE + msg);
	}

}
