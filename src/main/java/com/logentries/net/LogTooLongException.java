package com.logentries.net;

/**
 * Exception that extends RuntimeException and occurs when a log is extremely long and requires being added to the queue multiple times
 */
public class LogTooLongException extends RuntimeException {
}
