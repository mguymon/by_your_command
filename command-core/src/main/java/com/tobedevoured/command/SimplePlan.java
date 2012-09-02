package com.tobedevoured.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.tobedevoured.command.LogUtil;

/**
 * Default Plan used to execute a method on an {@link ByYourCommand} annotated Class
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

	/**
	 * Construct a new instance of that target
	 * 
	 * @return Object
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

	/**
	 * Add a {@link Command}
	 */
	public void addCommand( CommandMethod command ) {
		commands.put(command.getName(), command );
	}
	
	/**
	 * Execute the default Command with params for this Plan
	 * 
	 * @param List
	 * @return {@link CommandMethod} that was executed
	 */
	public CommandMethod exec( List<Object> params ) throws CommandException {
		if ( defaultCommand != null ) {
			exec( defaultCommand, params );
		} else {
			throw new CommandException( "Default method not set for " + target );
		}
		
		return defaultCommand;
	}

	/**
	 * Execute the Command for the String notation group:target:name with params.
	 * 
	 * @param commandNotaiton String
	 * @param params List
	 * @return {@link CommandMethod} that was executed
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

	/**
	 * Execute a {@link CommandMethod} with params
	 * 
	 * @param command {@link CommandMethod}
	 * @param params List
	 * @return {@link CommandMethod} that was executed
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

	/**
	 * List of descriptions for the commands in this Plan
	 * 
	 * @return List<String>
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

	public CommandMethod getDefaultCommand() {
		return defaultCommand;
	}

	public void setDefaultCommand(CommandMethod defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

	public Map<String, CommandMethod> getCommands() {
		return commands;
	}
	
	public void setCommands(Map<String, CommandMethod> commands) {
		this.commands = commands;
	}

	public Class getTarget() {
		return target;
	}

	public void setTarget(Class target) {
		this.target = target;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTargetGroup() {
		return targetGroup;
	}

	public void setTargetGroup(String targetGroup) {
		this.targetGroup = targetGroup;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append( "targetGroup", this.targetGroup )
			.append( "targetName", this.targetName )
			.append( "target", this.target )
			.append( "commandsDesc", this.commandsDesc() )
			.toString();
		
	}
}
