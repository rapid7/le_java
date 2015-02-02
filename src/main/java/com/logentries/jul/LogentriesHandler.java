package com.logentries.jul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import static java.util.logging.ErrorManager.CLOSE_FAILURE;
import static java.util.logging.ErrorManager.FORMAT_FAILURE;
import static java.util.logging.ErrorManager.GENERIC_FAILURE;
import static java.util.logging.ErrorManager.OPEN_FAILURE;
import static java.util.logging.ErrorManager.WRITE_FAILURE;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * <code>LogentriesHandler</code>: A handler for writing formatted records to a
 * logentries.com. This handler uses the Token-based input.
 *
 * @author Björn Raupach (raupach@me.com)
 */
public final class LogentriesHandler extends Handler {

    private String host;
    private int port;
    private byte[] token;
    private boolean open;
    private SocketChannel channel;
    private ByteBuffer buffer;
    private final byte[] newline = {0x0D, 0x0A};
    private final byte space = 0x020;

    public LogentriesHandler() {
        configure();
        connect();
        buffer = ByteBuffer.allocate(4096);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (open && isLoggable(record)) {
            String msg = formatMessage(record);
            if (!msg.isEmpty()) {
                boolean filled = fillAndFlip(msg);
                if (filled) {
                    boolean drained = drain();
                    if (!drained) {
                        System.err.println("java.util.logging.ErrorManager: Sending to logentries.com failed. Trying to reconnect once.");
                        connect();
                        if (open) {
                            filled = fillAndFlip(msg);
                            if (filled) {
                                drained = drain();
                                if (!drained) {
                                    System.err.println("java.util.logging.ErrorManager: Unable to reconnect. Shutting handler down.");
                                    close();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    String formatMessage(LogRecord record) {
        String msg = "";
        try {
            msg = getFormatter().format(record);
            // replace line separators with unicode equivalent
            msg = msg.replace(System.getProperty("line.separator"), "\u2028");
        } catch (Exception e) {
            reportError("Error while formatting.", e, FORMAT_FAILURE);
        }
        return msg;
    }

    boolean fillAndFlip(String formattedMessage) {
        try {
            buffer.clear();
            buffer.put(token);
            buffer.put(space);
            buffer.put(formattedMessage.getBytes(Charset.forName("UTF-8")));
            buffer.put(newline);
        } catch (BufferOverflowException e) {
            reportError("Buffer exceeds capacity", e, WRITE_FAILURE);
            return false;
        }
        buffer.flip();
        return true;
    }

    boolean drain() {
        while (buffer.hasRemaining()) {
            try {
                channel.write(buffer);
            } catch (Exception  e) {
                reportError("Error while writing channel.", e, WRITE_FAILURE);
                return false;
            }
        }
        return true;
    }

    void configure() {
        String cname = getClass().getName();
        setLevel(getLevelProperty(cname + ".level", Level.INFO));
        setFormatter(getFormatterProperty(cname + ".formatter", new SimpleFormatter()));
        setHost(getStringProperty(cname + ".host", "data.logentries.com"));
        setPort(getIntProperty(cname + ".port", 514));
        setToken(getBytesProperty(cname + ".token", ""));
    }

    void connect() {
        try {
            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(host, port));
            open = true;
        } catch (IOException e) {
            open = false; 
            reportError(MessageFormat.format("Error connection to host: {0}:{1}", host, port), e, OPEN_FAILURE);
        }
    }
    
    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {
        open = false;
        buffer = null;
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                reportError("Error while closing channel.", e, CLOSE_FAILURE);
            }
        }
    }
    
    // -- These methods are private in LogManager
    
    Level getLevelProperty(String name, Level defaultValue) {
        LogManager manager = LogManager.getLogManager();
        String val = manager.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        Level l = Level.parse(val.trim());
        return l != null ? l : defaultValue;
    }

    Formatter getFormatterProperty(String name, Formatter defaultValue) {
        LogManager manager = LogManager.getLogManager();
        String val = manager.getProperty(name);
        try {
            if (val != null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Class<?> clz = cl.loadClass(val);
                return (Formatter) clz.newInstance();
            }
        } catch (ClassNotFoundException e) {
            reportError(MessageFormat.format("Error reading property ''{0}''", name), e, GENERIC_FAILURE);
        } catch (InstantiationException e) {
            reportError(MessageFormat.format("Error reading property ''{0}''", name), e, GENERIC_FAILURE);
        } catch (IllegalAccessException e) {
            reportError(MessageFormat.format("Error reading property ''{0}''", name), e, GENERIC_FAILURE);
        }
        return defaultValue;
    }

    String getStringProperty(String name, String defaultValue) {
        LogManager manager = LogManager.getLogManager();
        String val = manager.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        return val.trim();
    }

    byte[] getBytesProperty(String name, String defaultValue) {
        return getStringProperty(name, defaultValue).getBytes();
    }

    int getIntProperty(String name, int defaultValue) {
        LogManager manager = LogManager.getLogManager();
        String val = manager.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            reportError(MessageFormat.format("Error reading property ''{0}''", name), e, GENERIC_FAILURE);
            return defaultValue;
        }
    }

}
