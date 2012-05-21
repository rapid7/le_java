package com.logentries.log4j;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Example {

	/** Create logger. */
	static Logger log = LogManager.getRootLogger();

	/**
	 * Log few lines.
	 * 
	 * @param args command line arguments
	 * @throws InterruptedException
	 */
	public static void main( String[] args) throws InterruptedException {
		
		System.err.println( "Sending warning messages, line by line...");
		
		log.info( "Sending warnings, line by line");
		
		for (int i=0; i<1000; i++) {
			log.warn( "Warning message " +i);
			Thread.sleep( 1000);	
		}
	}

}
