package com.tobedevoured.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Model of a Method annotation by @Command
 * 
 * @author Michael Guymon
 *
 */
public class CommandMethod {

	private String name;
	private String methodName;
	private Map<String, Class> paramTypes;
	private Map<String, Object> defaults;
	private boolean exit = true;
	private boolean logResult = false;
	private Object result;
	
	/**
	 * Construct with the CommandMethod's field's name
	 * 
	 * @param name String
	 */
	public CommandMethod( String name ) {
		this.name = name;
		this.paramTypes = new HashMap<String,Class>();
		this.defaults   = new HashMap<String,Object>();
	}
	
	/**
	 * Construct with the CommandMethod's name and the field's name
	 * 
	 * @param name String
	 * @param methodName String
	 */
	public CommandMethod( String name, String methodName ) {
		this.name = name;
		this.methodName = methodName;
		this.paramTypes = new HashMap<String,Class>();
		this.defaults   = new HashMap<String,Object>();
	}

	public String getName() {
		return name;
	}

	/**
	 * Add a method param
	 *  
	 * @param name String
	 * @param type Clazz
	 */
	public void addParam( String name, Class type ) {
		paramTypes.put( name, type );
	}
	
	/**
	 * Add a method param with a default value 
	 * 
	 * @param name String 
	 * @param type Class
	 * @param defaultValue Object
	 */
	public void addParam(String name, Class type, Object defaultValue) {
		addParam( name, type );
		defaults.put( name, defaultValue );
	}
	
	/**
	 * Map of method params
	 * 
	 * @return {@link Map<String,Class>}
	 */
	public Map<String, Class> getParamTypes() {
		return paramTypes;
	}

	public Class[] getParamTypesArray() {
		return paramTypes.values().toArray( new Class[paramTypes.size()] );
	}
	
	public Map<String,Object> getDefaults() {
		return defaults;
	}
	
	public boolean hasParams() {
		return paramTypes.size() > 0; 
	}

	public String getMethodName() {
		return methodName;
	}

	/**
	 * Should this CommandMethod exit after execution
	 * 
	 * @return boolean
	 */
	public boolean isExit() {
		return exit;
	}

	/**
	 * Set if this CommandMethod should exist after execution
	 * 
	 * @return boolean
	 */
	public void setExit(boolean exit) {
		this.exit = exit;
	}

	/**
	 * Should this CommandMethod log the result of the execution
	 * 
	 * @return boolean
	 */
	public boolean isLogResult() {
		return logResult;
	}

	/**
	 * Set if this CommandMethod should log the result of the execution
	 * 
	 * @param logResult boolean
	 */
	public void setLogResult(boolean logResult) {
		this.logResult = logResult;
	}

	/**
	 * Get the result of the CommandMethod's execution
	 * 
	 * @return Object
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * Set the result of the CommandMethod's execution
	 * 
	 * @param result Object
	 */
	public void setResult(Object result) {
		this.result = result;
	}
}
