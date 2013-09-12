package com.logentries.jenkins;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class to write logs to logentries asynchronously
 * 
 */
public class AsynchronousLogentriesWriter implements LogentriesWriter {

	private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

	private final ExecutorService executor;
	private final LogentriesWriter leWriter;

	/**
	 * Constructor.
	 * @param leWriter Used to write entries to Logentries.
	 */
	public AsynchronousLogentriesWriter(LogentriesWriter leWriter) {
		this.executor = Executors.newSingleThreadExecutor();
		this.leWriter = leWriter;
	}

	/**
	 * Writes the given string to logentries.com asynchronously. It would be
	 * possible to take an array of bytes as a parameter but we want to make
	 * sure it is UTF8 encoded.
	 * 
	 * @param line The line to write.
	 */
	public void writeLogentry(final String line) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					leWriter.writeLogentry(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS,
					TimeUnit.SECONDS)) {
				System.err
						.println("LogentriesWriter shutdown before finished execution");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			leWriter.close();
		}
	}
}
