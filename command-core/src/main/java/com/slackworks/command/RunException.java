package com.slackworks.command;

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
