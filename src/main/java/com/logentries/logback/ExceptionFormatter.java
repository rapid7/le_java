package com.logentries.logback;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

/**
 * Formatter to generate Logentries-compatible stack traces.
 *
 * <p>The Logentries TCP token input is delimited by a traditional newline '\n'
 * char, so multiline events like stack traces should use the unicode line
 * separator.</p>
 *
 * <p>This class simplifies the coercion of logged exceptions into events that
 * can be easily viewed and searched through <a
 * href="https://logentries.com">Logentries</a>.</p>
 *
 * @author Chris Mowforth
 */
public class ExceptionFormatter {

	/** Logentries newline delimiter. */
	public static final String DELIMITER = "\u2028";
	/** Tab character used for trace indentation. */
	public static final String TAB = "\t";

	/**
	 * Returns a formatted stack trace for an exception.
	 *
	 * <p>This method provides a full (non-truncated) trace delimited by
	 * {@link #DELIMITER}. Currently it doesn't make any use of Java 7's <a
	 * href="http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html#suppressed-exceptions">exception
	 * suppression</a>.</p>
	 *
	 * @param error an {@link IThrowableProxy} object
	 * @return stack trace string
	 */
	public static String formatException(IThrowableProxy error) {
		String ex = "";
		ex += formatTopLevelError(error);
		ex += formatStackTraceElements(error.getStackTraceElementProxyArray());
		IThrowableProxy cause = error.getCause();
        ex += DELIMITER;
		while (cause != null) {
			ex += formatTopLevelError(cause);
			StackTraceElementProxy[] arr = cause.getStackTraceElementProxyArray();
			ex += formatStackTraceElements(arr);
			ex += DELIMITER;
			cause = cause.getCause();
		}
		return ex;
	}

	private static String formatStackTraceElements(StackTraceElementProxy[] elements) {
		String s = "";
        if (elements != null) {
            for (StackTraceElementProxy e : elements) {
                s += DELIMITER + TAB + e.getSTEAsString();
            }
        }
		return s;
	}

	private static String formatTopLevelError(IThrowableProxy error) {
		return error.getClassName() + ": " + error.getMessage();
	}
}
