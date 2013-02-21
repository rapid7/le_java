package com.logentries.logback;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogConstants;

/**
 * Logentries appender for logback.
 * 
 * VERSION: 1.1.7
 * 
 * @author Viliam Holub
 * @author Mark Lacomber
 * @author Ben McCann
 */
public class LogentriesAppender extends AppenderBase<ILoggingEvent> {

  /*
   * Constants
   */

  /** Current Version number of library/ */
  static final String VERSION = "1.1.7";
  /** Size of the internal event queue. */
  private static final int QUEUE_SIZE = 32768;
  /** Logentries API server address. */
  private static final String LE_API = "api.logentries.com";
  /** Logentries local API server address for testing. */
  private static final String LE_LOCAL_API = "localhost";
  /** Port number for Token logging on Logentries API server. */
  private static final int LE_PORT = 10000;
  /** Local port number for testing. */
  private static final int LE_LOCAL_PORT = 8088;
  /** UTF-8 output character set. */
  private static final Charset UTF8 = Charset.forName("UTF-8");
  /** Minimal delay between attempts to reconnect in milliseconds. */
  private static final int MIN_DELAY = 100;
  /** Maximal delay between attempts to reconnect in milliseconds. */
  private static final int MAX_DELAY = 10000;
  /** LE appender signature - used for debugging messages. */
  private static final String LE = "LE ";
  /** Error message displayed when invalid API key is detected. */
  private static final String INVALID_TOKEN = "\n\nIt appears your LOGENTRIES_TOKEN parameter in log4j.xml is incorrect!\n\n";
  /** Key Value for Token Environment Variable. */
  private static final String CONFIG_TOKEN = "LOGENTRIES_TOKEN";
  /** Platform dependent line separator to check for. Supported in Java 1.6+ */
  private static final String LINE_SEP = System.getProperty("line_separator", "\n");
  /** Error message displayed when queue overflow occurs */
  private static final String QUEUE_OVERFLOW = "\n\nLogentries Buffer Queue Overflow. Message Dropped!\n\n";

  static final public String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";

  /*
   * Fields
   */

  /** Destination Token. */
  String token = "";
  /** Debug flag. */
  boolean debug;
  /** Make local connection only. */
  boolean local;
  // /** Indicator if the socket appender has been started. */
  boolean started;
  /** Layout */
  Layout<ILoggingEvent> layout;
  String facilityStr;
  /** Asynchronous socket appender. */
  SocketAppender appender;
  /** Message queue. */
  ArrayBlockingQueue<String> queue;

  protected String suffixPattern;

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
      super("Logentries log4j appender");
      // Don't block shut down
      setDaemon(true);
    }

    /**
     * Opens connection to Logentries.
     * 
     * @throws IOException
     */
    void openConnection() throws IOException {
      final String api_addr = local ? LE_LOCAL_API : LE_API;
      final int port = local ? LE_LOCAL_PORT : LE_PORT;

      dbg("Reopening connection to Logentries API server " + api_addr + ":" + port);

      // Open physical connection
      socket = new Socket(api_addr, port);

      stream = socket.getOutputStream();

      dbg("Connection established");
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
            dbg("Unable to connect to Logentries");
            e.printStackTrace();
          }
        }

        // Wait between connection attempts
        root_delay *= 2;
        if (root_delay > MAX_DELAY)
          root_delay = MAX_DELAY;
        int wait_for = root_delay + random.nextInt(root_delay);
        dbg("Waiting for " + wait_for + "ms");
        Thread.sleep(wait_for);
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

          // Replace platform-independent carriage return with unicode line
          // separator character to format multi-line events nicely in
          // Logentries UI
          data = data.replace(LINE_SEP, "\u2028");

          // Add newline to end the event
          data += '\n';

          // Get bytes of final event
          byte[] finalLine = data.getBytes(UTF8);

          // Send data, reconnect if needed
          while (true) {
            try {
              stream.write(finalLine);
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
        dbg("Asynchronous socket writer interrupted");
      }

      closeConnection();
    }
  }

  /**
   * Initializes asynchronous logging.
   * 
   * @param local
   *          make local connection to API server for testing
   */
  LogentriesAppender(boolean local) {
    this.local = local;

    queue = new ArrayBlockingQueue<String>(QUEUE_SIZE);

    appender = new SocketAppender();
  }

  /**
   * Initializes asynchronous logging.
   */
  public LogentriesAppender() {
    this(false);
  }

  public void start() {
    if (layout == null) {
      layout = buildLayout();
    }
    super.start();
  }

  String getPrefixPattern() {
    return "%syslogStart{" + getFacility() + "}%nopex";
  }

  /**
   * Returns the string value of the <b>Facility</b> option.
   * 
   * See {@link #setFacility} for the set of allowed values.
   */
  public String getFacility() {
    return facilityStr;
  }

  /**
   * The <b>Facility</b> option must be set one of the strings KERN, USER, MAIL,
   * DAEMON, AUTH, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP, NTP, AUDIT,
   * ALERT, CLOCK, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6,
   * LOCAL7. Case is not important.
   * 
   * <p>
   * See {@link SyslogConstants} and RFC 3164 for more information about the
   * <b>Facility</b> option.
   */
  public void setFacility(String facilityStr) {
    if (facilityStr != null) {
      facilityStr = facilityStr.trim();
    }
    this.facilityStr = facilityStr;
  }
  
  /**
   * Checks that key and location are set.
   */
  boolean checkCredentials() {
    if (token.equals(CONFIG_TOKEN) || token.equals("")) {
      // Check if set in an environment variable
      String envToken = System.getProperty(CONFIG_TOKEN);

      if (envToken == null)
        return false;

      token = envToken;
    }

    // Quick test to see if LOGENTRIES_TOKEN is a valid UUID
    UUID u = UUID.fromString(token);
    if (!u.toString().equals(token)) {
      dbg(INVALID_TOKEN);
      return false;
    }

    return true;
  }

  /**
   * Sets the token
   * 
   * @param token
   *          new token
   */
  public void setToken(String token) {
    this.token = token;
    dbg("Setting token to " + token);
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
   * @param debug
   *          debug flag to set
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
    dbg("Setting debug to " + debug);
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
   * @param line
   *          line to append
   */
  void appendLine(String line) {
    dbg("Queueing " + line);

    // Prefix the data with Token
    String data = token + line;

    // Try to append data to queue
    if (!queue.offer(data)) {
      queue.poll();
      if (!queue.offer(data))
        dbg(QUEUE_OVERFLOW);
    }
  }

  /**
   * Implements AppenderSkeleton Append method, handles time and format
   * 
   * @event event to log
   */
  @Override
  protected void append(ILoggingEvent event) {
    // Check that we have all parameters set and socket appender running
    if (!started && checkCredentials()) {
      dbg("Starting Logentries asynchronous socket appender");
      appender.start();
      started = true;
    }

    // Render the event according to layout
    String formattedEvent = layout.doLayout(event);

    // Append stack trace if present
    if (event.getThrowableProxy() != null) {
      StackTraceElement[] stack = event.getCallerData();
      int len = stack.length;
      for (int i = 0; i < len; i++) {
        formattedEvent += "\u2028\tat " + stack[i].getClassName() + "." + stack[i].getMethodName()
            + "(" + stack[i].getFileName() + ":" + stack[i].getLineNumber() + ")";
      }
    }

    // Prepare to be queued
    appendLine(formattedEvent);
  }

  /**
   * Closes all connections to Logentries
   */
  @Override
  public void stop() {
    super.stop();
    appender.interrupt();
    dbg("Closing Logentries asynchronous socket appender");
  }

  /**
   * Prints the message given. Used for internal debugging.
   * 
   * @param msg
   *          message to display
   */
  void dbg(String msg) {
    if (debug)
      System.err.println(LE + msg);
  }

  public Layout<ILoggingEvent> buildLayout() {
    PatternLayout layout = new PatternLayout();
    layout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
    if (suffixPattern == null) {
      suffixPattern = DEFAULT_SUFFIX_PATTERN;
    }
    layout.setPattern(getPrefixPattern() + suffixPattern);
    layout.setContext(getContext());
    layout.start();
    return layout;
  }

/**
   * See {@link #setSuffixPattern(String).
   * 
   * @return
   */
  public String getSuffixPattern() {
    return suffixPattern;
  }

  /**
   * The <b>suffixPattern</b> option specifies the format of the
   * non-standardized part of the message sent to the syslog server.
   * 
   * @param suffixPattern
   */
  public void setSuffixPattern(String suffixPattern) {
    this.suffixPattern = suffixPattern;
  }

}
