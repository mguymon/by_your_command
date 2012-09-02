package com.tobedevoured.command;

import java.util.List;
import java.util.Map;

/**
 * Plan used to execute a method on an {@link ByYourCommand} annotated Class
 * 
 * @author Michael Guymon
 *
 */
public interface Planable {

	/**
	 * Get the target for this Plan
	 * 
	 * @return Object
	 * @throws CommandException
	 */
	public abstract Object buildTarget() throws CommandException;

	/**
	 * Add a {@link CommandMethod}
	 * 
	 * @param command {@link CommandMethod}
	 */
	public abstract void addCommand(CommandMethod command);

	/**
	 * Exec default {@link CommandMethod}
	 * 
	 * @param params {@link List}
	 * @return {@link CommandMethod}
	 * @throws CommandException
	 */
	public abstract CommandMethod exec(List<Object> params)
			throws CommandException;

	/**
	 * Exec a {@link CommandMethod} for notation
	 * 
	 * @param commandNotation String
	 * @param params {@link List}
	 * @return {@link CommandMethod}
	 * @throws CommandException
	 */
	public abstract CommandMethod exec(String commandNotation,
			List<Object> params) throws CommandException;

	/**
	 * Exec a {@link CommandMethod}
	 * 
	 * @param command {@link CommandMethod}
	 * @param params {@link List}
	 * @return {@link CommandMethod}
	 * @throws CommandException
	 */
	public abstract CommandMethod exec(CommandMethod command,
			List<Object> params) throws CommandException;

	/**
	 * {@link List<String>} of all commands for this Plan
	 * 
	 * @return {@link List<String>}
	 */
	public abstract List<String> commandsDesc();

	public abstract CommandMethod getDefaultCommand();

	public abstract void setDefaultCommand(CommandMethod defaultCommand);

	public abstract Map<String, CommandMethod> getCommands();

	public abstract void setCommands(Map<String, CommandMethod> commands);

	public abstract Class getTarget();

	public abstract void setTarget(Class target);

	public abstract String getTargetName();

	public abstract void setTargetName(String targetName);

	public abstract String getTargetGroup();

	public abstract void setTargetGroup(String targetGroup);

}