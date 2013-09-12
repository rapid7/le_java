package com.logentries.jenkins;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.ssl.SSLSocketFactory;

/**
 * 
 * Writes lines to Logentries using the Token TCP input
 */
public class LogentriesTcpTokenWriter implements LogentriesWriter {
	
	/** Logentries API server address. */
	private static final String LE_API = "api.logentries.com";
	/** Port number for Token logging on Logentries API server. */
	private static final int LE_PORT = 20000;
	/** UTF-8 output character set. */
	private static final Charset UTF8 = Charset.forName("UTF-8");

	private final String token;
	private final Socket socket;
	private final OutputStream outputStream;
	
	/**
	 * Constructor
	 * @param token The token for the logfile
	 * @throws IOException If there was a problem connecting to logentries.
	 */
	public LogentriesTcpTokenWriter(String token) throws IOException {
		this.token = token;
		socket = SSLSocketFactory.getDefault().createSocket(LE_API, LE_PORT);
		outputStream = socket.getOutputStream();
	}
	
	/**
	 * Write the given line to Logentries.
	 * @param line The line to write.
	 * @throws IOException If there was a problem writing the line.
	 */
	public void writeLogentry(final String line) throws IOException {
		outputStream.write((token + line + '\n').getBytes( UTF8));
		outputStream.flush();
	}
	
	/**
	 * Closes this writer.
	 * TODO implement Closeable?
	 */
	public void close() {
		closeStream();
		closeSocket();
	}
	
	private void closeStream() {
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
