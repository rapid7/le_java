package com.logentries.net;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Client for sending messages to Logentries via HTTP PUT or Token-Based Logging
 * Supports SSL/TLS
 *
 * @author Mark Lacomber
 */
public class LogentriesClient {
    /**
     * Logentries API server address for Token-based input.
     */
    private static final String LE_TOKEN_API = "data.logentries.com";
    /**
     * Logentries API server address for HTTP PUT input.
     */
    private static final String LE_HTTP_API = "api.logentries.com";
    /**
     * Port number for HTTP PUT/Token TCP logging on Logentries server.
     */
    private static final int LE_PORT = 80;
    /**
     * Port number for SSL HTTP PUT/TLS Token TCP logging on Logentries server.
     */
    private static final int LE_SSL_PORT = 443;

    private final SSLSocketFactory ssl_factory;
    private boolean ssl_choice = false;
    private boolean http_choice = false;
    private Socket socket;
    private OutputStream outputStream;
    private String dataHubServer = LE_TOKEN_API;
    private int dataHubPort = LE_PORT;
    private boolean useDataHub = false;

    public LogentriesClient(boolean httpPut, boolean ssl, boolean isUsingDataHub, String server, int port) {
        if (isUsingDataHub) {
            ssl_factory = null; // DataHub does not support input over SSL for now,
            ssl_choice = false; // so SSL flag is ignored
            useDataHub = true;
            dataHubServer = server;
            dataHubPort = port;
        } else {
            ssl_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            ssl_choice = ssl;
            http_choice = httpPut;
        }
    }

    public int getPort() {
        if (useDataHub) {
            return dataHubPort;
        } else {
            return ssl_choice ? LE_SSL_PORT : LE_PORT;
        }
    }

    public String getAddress() {
        if (useDataHub) {
            return dataHubServer;
        } else {
            return http_choice ? LE_HTTP_API : LE_TOKEN_API;
        }
    }

    public void connect() throws IOException {
        if (ssl_choice) {
            if (http_choice) {
                SSLSocket s = (SSLSocket) ssl_factory.createSocket(getAddress(), getPort());
                s.startHandshake();
                socket = s;
            } else {
                socket = ssl_factory.createSocket(getAddress(), getPort());
            }
        } else {
            socket = new Socket(getAddress(), getPort());
        }

        this.socket.setKeepAlive(true);
        this.outputStream = socket.getOutputStream();
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        if (this.outputStream == null) {
            throw new IOException();
        }

        this.outputStream.write(buffer, offset, length);
        this.outputStream.flush();
    }

    public void close() {
        try {
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
        } catch (Exception e) {

        }
    }
}
