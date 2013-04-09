package com.logentries.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogConstants;

import com.logentries.net.AsyncLogger;

/**
* Logentries appender for logback.
*
* @author Mark Lacomber
* @author Ben McCann
*/
public class LogentriesAppender extends AppenderBase<ILoggingEvent> {
	
	/*
	 * Fields
	 */
	/** Asynchronous Background logger */
	AsyncLogger le_async;
	/** Layout */
	Layout<ILoggingEvent> layout;
	/** Facility String */
	String facilityStr;
	/** Default Suffix Pattern */
	static final public String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";

	protected String suffixPattern;

	/**
	 * Initializes asynchronous logging.
	 */
	public LogentriesAppender() 
	{
		le_async = new AsyncLogger();
	}
	
	/*
	 * Public methods to send logback parameters to AsyncLogger
	 */
	/**
	 * Sets the token
	 * 
	 * @param token
	 */
	public void setToken( String token) {
		this.le_async.setToken(token);
	}
	
	/**
	 *  Sets the HTTP PUT boolean flag. Send logs via HTTP PUT instead of default Token TCP
	 *  
	 *  @param httpput HttpPut flag to set
	 */
	public void setHttpPut( boolean HttpPut) {
		this.le_async.setHttpPut(HttpPut);
	}
	
	/** Sets the ACCOUNT KEY value for HTTP PUT 
	 * 
	 * @param account_key
	 */
	public void setKey( String account_key)
	{
		this.le_async.setKey(account_key);
	}
	
	/**
	 * Sets the LOCATION value for HTTP PUT
	 * 
	 * @param log_location
	 */
	public void setLocation( String log_location)
	{
		this.le_async.setLocation(log_location);
	}
	
	/**
	 * Sets the SSL boolean flag
	 * 
	 * @param ssl
	 */
	public void setSsl( boolean ssl)
	{
		this.le_async.setSsl(ssl);
	}
	
	/**
	 * Sets the debug flag. Appender in debug mode will print error messages on
	 * error console.
	 * 
	 * @param debug debug flag to set
	 */
	public void setDebug( boolean debug) {
		this.le_async.setDebug(debug);
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
	 *  Sets the layout for the Appender
	 */
	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}

	/**
	 * Implements AppenderSkeleton Append method, handles time and format
	 *
	 * @event event to log
	 */
	@Override
	protected void append(ILoggingEvent event) {
		
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
		this.le_async.addLineToQueue(formattedEvent);
	}

	/**
	 * Closes all connections to Logentries
	 */
	@Override
	public void stop() {
		super.stop();
		this.le_async.close();
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
