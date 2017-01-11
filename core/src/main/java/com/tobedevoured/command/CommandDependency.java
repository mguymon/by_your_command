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
	 * Get the Set of resource paths to Spring contexts
	 * 
	 * @return Set
	 */
	public Set<String> getContexts() {
		return contexts;
	}
	
	/**
	 * Set the SEt of resource paths to Spring contexts
	 * 
	 * @param contexts Set
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
