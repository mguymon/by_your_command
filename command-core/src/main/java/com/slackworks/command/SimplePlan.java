package com.slackworks.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.slackworks.command.LogUtil;

/**
 * Plan for executing {@link CommandMethod} on target Bean 
 * 
 * @author Michael Guymon
 */
public class SimplePlan implements Planable {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected CommandMethod defaultCommand;
	protected Map<String, CommandMethod> commands;
	protected Class target;
	protected String targetName;
	protected String targetGroup;	
	
	/**
	 * Construct new instance
	 */
	public SimplePlan() {
		commands = new HashMap<String, CommandMethod>();
	}
	
	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#buildTarget()
	 */

	public Object buildTarget() throws CommandException {
		try {
			return ConstructorUtils.invokeConstructor( target, null );
		} catch (NoSuchMethodException e) {
			throw new CommandException(e);
		} catch (IllegalAccessException e) {
			throw new CommandException(e);
		} catch (InvocationTargetException e) {
			throw new CommandException(e);
		} catch (InstantiationException e) {
			throw new CommandException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#addCommand(com.slackworks.command.CommandMethod)
	 */

	public void addCommand( CommandMethod command ) {
		commands.put(command.getName(), command );
	}
	
	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#exec(java.util.List)
	 */

	public CommandMethod exec( List<Object> params ) throws CommandException {
		if ( defaultCommand != null ) {
			exec( defaultCommand, params );
		} else {
			throw new CommandException( "Default method not set for " + target );
		}
		
		return defaultCommand;
	}
	
	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#exec(java.lang.String, java.util.List)
	 */

	public CommandMethod exec( String commandNotation, List<Object> params ) throws CommandException {
		String[] notation = commandNotation.split(":");
		
		CommandMethod command;
		
		// Without a colon, this as a command, not as a group or target
		if ( notation.length == 1 ) {
			command = commands.get( commandNotation );
			exec( command, params );
			
		// Treat this as a group:target, which means run the default
		} else if ( notation.length == 2 ) {
			command = defaultCommand;
			exec(params);
			
		// Treat this as a group:target:command
		} else if ( notation.length == 3 ) {
			command = commands.get( notation[2] );
			exec( command, params );
			
		} else {
			throw new CommandException( "Malformed command: " + commandNotation );
		}
		
		return command;
	}
		
	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#exec(com.slackworks.command.CommandMethod, java.util.List)
	 */

	public CommandMethod exec( CommandMethod command, List<Object> params) throws CommandException {
		
		Object instance = buildTarget();
		Method method = null;
		try {
			method = instance.getClass().getMethod( command.getMethodName(), command.getParamTypesArray() );
		} catch (SecurityException e) {
			throw new CommandException( e );
		} catch (NoSuchMethodException e) {
			throw new CommandException( e );
		}
	
		logger.debug( "execing method {} for class {}", method.getName(), target.getName() );
	
		Object result = null;
		try {
			if ( params != null ) {
				result = method.invoke( instance, params.toArray() );
			} else {
				result = method.invoke( instance );
			}
		} catch (IllegalArgumentException e) {
			throw new CommandException( e );
		} catch (IllegalAccessException e) {
			throw new CommandException( e );
		} catch (InvocationTargetException e) {
			throw new CommandException( e );
		}
		
		command.setResult( result );
		
		if ( command.isLogResult() ) {
			Level level = LogUtil.currentRootLevel();
			LogUtil.changeRootLevel( Level.INFO );
			LoggerFactory.getLogger( this.getTarget() ).info( "{} [{}]", command.getName(), result );
			LogUtil.changeRootLevel( level );
		}
		
		return command;
	}
	
	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#commandsDesc()
	 */

	public List<String> commandsDesc() {
		List<String> commandsDesc = new ArrayList<String>();
		
		if ( defaultCommand != null ) {
			commandsDesc.add(  new StringBuilder( targetGroup )
				.append( ":" ).append( targetName ).toString() );
		}
		
		for ( String command : commands.keySet() ) {
			StringBuilder desc = new StringBuilder( targetGroup )
				.append( ":" ).append( targetName ).append(":").append(command);
			
			CommandMethod commandMethod = commands.get( command );
			if ( commandMethod.getParamTypes().size() > 0 ) {
				desc.append( "[" );
				for( String name : commandMethod.getParamTypes().keySet() ) {
					desc.append( name ).append( ", ");
				}
				desc.setLength(desc.length() - 2);
				desc.append("]");
			}
			
			commandsDesc.add( desc.toString() );
		}
		
		return commandsDesc;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#getDefaultCommand()
	 */

	public CommandMethod getDefaultCommand() {
		return defaultCommand;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#setDefaultCommand(com.slackworks.command.CommandMethod)
	 */

	public void setDefaultCommand(CommandMethod defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#getCommands()
	 */

	public Map<String, CommandMethod> getCommands() {
		return commands;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#setCommands(java.util.Map)
	 */

	public void setCommands(Map<String, CommandMethod> commands) {
		this.commands = commands;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#getTarget()
	 */

	public Class getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#setTarget(java.lang.Class)
	 */

	public void setTarget(Class target) {
		this.target = target;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#getTargetName()
	 */

	public String getTargetName() {
		return targetName;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#setTargetName(java.lang.String)
	 */

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#getTargetGroup()
	 */

	public String getTargetGroup() {
		return targetGroup;
	}

	/* (non-Javadoc)
	 * @see com.slackworks.command.Planable#setTargetGroup(java.lang.String)
	 */

	public void setTargetGroup(String targetGroup) {
		this.targetGroup = targetGroup;
	}
}
