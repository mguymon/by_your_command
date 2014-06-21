package com.tobedevoured.command;

/**
 * Run Exception
 * 
 * @author Michael Guymon
 *
 */
public class RunException extends Exception {

	public RunException(String message) {
		super(message);
	}

	public RunException(Throwable cause) {
		super(cause);
	}

	public RunException(String message, Throwable cause) {
		super(message, cause);
	}

}
