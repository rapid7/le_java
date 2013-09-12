package com.logentries.jenkins;

import java.io.IOException;

/**
 * Writes lines to Logentries. 
 *
 */
public interface LogentriesWriter {

	/**
	 * Writes the given line to Logentries.
	 * @param line The line to write.
	 * @throws IOException If there was a problem writing the line.
	 */
	void writeLogentry(final String line) throws IOException;
	
	/**
	 * Closes this writer.
	 */
	void close();
}
