package com.logentries.logback;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

public class ExceptionFormatter {

	public static final String DELIMITER = "\u2028";
	public static final String TAB = "\t";

	public static String formatException(IThrowableProxy error) {
		String ex = "";
		ex += formatTopLevelError(error);
		ex += formatStackTraceElements(error.getStackTraceElementProxyArray());
		ex += DELIMITER;
		IThrowableProxy cause = error.getCause();
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
		for (StackTraceElementProxy e : elements) {
			s += DELIMITER + TAB + e.getSTEAsString();
		}
		return s;
	}

	private static String formatTopLevelError(IThrowableProxy error) {
		return error.getClassName() + ": " + error.getMessage();
	}
}