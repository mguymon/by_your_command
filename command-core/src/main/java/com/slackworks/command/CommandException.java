package com.slackworks.command;

/**
 * Exec Exception
 * 
 * @author Michael Guymon
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = -2304343317798243074L;

	public CommandException( String message ) {
		super( message );
	}
	
	public CommandException( String message, Throwable throwable ) {
		super( message, throwable );
	}
	
	public CommandException( Throwable throwable ) {
		super( throwable );
	}
}
