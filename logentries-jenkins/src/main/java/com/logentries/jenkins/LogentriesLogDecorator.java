package com.logentries.jenkins;

import hudson.console.LineTransformationOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class LogentriesLogDecorator extends LineTransformationOutputStream {

	private final OutputStream wrappedOutputStream;
	private final LogentriesWriter leWriter;
	
	/**
	 * Constructor
	 * @param os The OutputStream to decorate
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public LogentriesLogDecorator(OutputStream os, LogentriesWriter leWriter) 
			throws UnknownHostException, IOException {
		this.wrappedOutputStream = os;
		this.leWriter = leWriter;
	}
	
	/**
	 * Called when the end of a line is reached.
	 */
	@Override
	protected void eol(byte[] bytes, int length) {
		try {
			processLine(bytes, length);
		} catch (IOException e) {
			// Just print out a trace
			e.printStackTrace();
		} catch (RuntimeException re) {
			// Don't break the build. Just print out a stack trace.
			re.printStackTrace();
		}
	}
	
	// Should we close this here?
	@Override
	public void close() throws IOException {
		leWriter.close();
		super.close();
		wrappedOutputStream.close();
	}
	
	private void processLine(byte[] bytes, int length) throws IOException {
		if (length > 0) {
			// Find the end before the new line
			int end = length - 1;
			while (bytes[end] == '\n' || bytes[end] == '\r') {
				end--;
			}
			// TODO Verify that the byte are encoded using the platform default (not UTF8)
			if (end > 0) {
				leWriter.writeLogentry(new String(bytes, 0, end + 1));
				wrappedOutputStream.write(bytes, 0, length);
			}
		}
	}
	
}
