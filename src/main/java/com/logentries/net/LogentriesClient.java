package com.logentries.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Client for sending messages to Logentries via HTTP PUT or Token-Based Logging
 * Supports SSL/TLS
 * 
 * @author Mark Lacomber
 * 
 */
public class LogentriesClient
{
	/*
	 * Constants
	 */
	
	/** Logentries API server address for Token-based input. */
	private static final String LE_TOKEN_API = "data.logentries.com";
	/** Logentries API server address for HTTP PUT input. */
	private static final String LE_HTTP_API = "api.logentries.com";
	/** Port number for HTTP PUT/Token TCP logging on Logentries server. */
	private static final int LE_PORT = 80;
	/** Port number for SSL HTTP PUT/TLS Token TCP logging on Logentries server. */
	private static final int LE_SSL_PORT = 443;

	final SSLSocketFactory ssl_factory;
	private boolean ssl_choice = false;
	private boolean http_choice = false;
	private Socket socket;
	private OutputStream stream;
	
	public LogentriesClient(boolean httpPut, boolean ssl)
	{
		ssl_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		ssl_choice = ssl;
		http_choice = httpPut;
	}

	public int getPort()
	{
		return ssl_choice ? LE_SSL_PORT : LE_PORT;
	}

	public String getAddress()
	{
		return http_choice ? LE_HTTP_API : LE_TOKEN_API;
	}
	
	public void connect() throws UnknownHostException, IOException
	{
		// Open physical connection
		if(ssl_choice) {
			if(http_choice)
			{
				SSLSocket s = (SSLSocket) ssl_factory.createSocket( getAddress(), getPort() );
				s.setTcpNoDelay( true);
				s.startHandshake();
				socket = s;
			}else{
				socket = SSLSocketFactory.getDefault().createSocket( getAddress(), getPort() );
			}
		}else{
			socket = new Socket( getAddress(), getPort() );
		}
		
		this.stream = socket.getOutputStream();
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException
	{
		if(this.stream == null){
			throw new IOException();
		}
		this.stream.write(buffer, offset, length);
		this.stream.flush();
	}
	
	public void close()
	{
		try{
			if (this.socket != null)
			{
				this.socket.close();
				this.socket = null;
			}
		}catch(Exception e){
			
		}
	}
}
