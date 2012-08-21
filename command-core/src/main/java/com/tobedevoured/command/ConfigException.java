package com.tobedevoured.command;

public class ConfigException extends Exception {

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

}
