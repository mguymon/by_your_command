package com.tobedevoured.command;

import java.util.HashSet;
import java.util.Set;

/**
 * The Dependencies required to execute a {@link CommandMethod}
 * 
 * @author Michael Guymon
 */
public class CommandDependency {

	private Class<?> target;
	private Set<String> contexts = new HashSet<String>();
	
	/**
	 * Get the target Class for the {@link CommandMethod}
	 * 
	 * @return Class
	 */
	public Class<?> getTarget() {
		return target;
	}
	
	/**
	 * Set the target Class for the {@link CommandMethod}
	 * 
	 * @param target Class
	 */
	public void setTarget(Class<?> target) {
		this.target = target;
	}
	
	/**
	 * Get the {@link Set<String>} of resource paths to Spring contexts
	 * 
	 * @return {@link Set<String>}
	 */
	public Set<String> getContexts() {
		return contexts;
	}
	
	/**
	 * Set the {@link Set<String>} of resource paths to Spring contexts
	 * 
	 * @param contexts {@link Set<String>}
	 */
	public void setContexts(Set<String> contexts) {
		this.contexts = contexts;
	}
	
	@Override
	public String toString() {
		return "ExecCommandDependency [target=" + target + ", contexts="
				+ contexts + "]";
	}
	
}
