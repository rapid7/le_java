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
		log.info( "Info log sent from java class");
		log.warn( "Warning log sent from java class");
		
		System.out.println( "Messages sent, via " +LeAppender.class);
		System.out.println( System.getProperty( "user.dir"));
		Thread.sleep( 1000);
	}

}
