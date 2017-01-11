package com.tobedevoured.command;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.ConstructorUtils;
import org.modeshape.common.text.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.ByYourCommandGroup;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.command.annotation.CommandParam;
import com.tobedevoured.command.annotation.CommandParams;

/**
 * Manages the creation and execution of commands.
 * 
 * @author Michael Guymon
 */
public class ByYourCommandManager {

    private static Logger logger = LoggerFactory.getLogger(ByYourCommandManager.class);
    
    private final static String[] EMPTY_STRING_ARRAY = new String[] { "" };
    
    // Commands and their Dependencies
    Map<String, CommandDependency> commands;
    
    // Class and the Plan to execute them
    private Map<Class, Plan> plans;
    
    // Groups and their Commands
    private Map<String, Set<String>> groups;
    
    // Description of every command
    private Set<String> commandsDesc;
    
    private Inflector inflector;

    private DependencyResolvable dependencyResolver;
    
    /**
     * Construct new instance
     */
    public ByYourCommandManager() {
        inflector = new Inflector();
        plans = new HashMap<Class, Plan>();
        commands = new HashMap<String, CommandDependency>();
        commandsDesc = new TreeSet<String>();
        groups = new HashMap<String, Set<String>>();

        // Create default dep manager
        dependencyResolver = buildDependencyResolver();
        dependencyResolver.setManager(this);
    }

    public DependencyResolvable buildDependencyResolver() {
        return new DefaultDependencyResolver();
    }
    
    /**
     * Create a default Plan
     *  
     * @return {@link Plan}
     */
    public Plan createObjectPlan() {
        return new Plan();
    }
    
    /**
     * Scans a List of packages for @ByYourCommand annotated Classes
     * 
     * @param packages List
     * @throws CommandException fails scan for commands
     */
    public void scanForCommands( List<String> packages ) throws CommandException {
        for( String _package: packages ) {
            scanForCommands( _package );
        }
    }
    
    /**
     * Scan a package for classes annotated with @ByYourCommand and register them.
     * 
     * @param _package String
     * @throws CommandException fails to scan for commands
     */
    public void scanForCommands( String _package ) throws CommandException {
        Set<Class<?>> execCommands = null;
        try {
            execCommands = new ClassScanner().findAnnotatedClasses( _package, ByYourCommand.class);
        } catch (IOException e) {
            throw new CommandException(e);
        }
        
        logger.debug( "Scanned {} and found {}", _package, execCommands );
        
        for ( Class clazz : execCommands ) {
            logger.debug( "Parsing class: {}", clazz );
            ByYourCommand execCommand = (ByYourCommand)clazz.getAnnotation( ByYourCommand.class );

            // Create ExecCommandDependency for annotated class
            CommandDependency execCommandDependency = newCommandDependency(clazz);
            execCommandDependency.setTarget( clazz );
            
            // Plan that buids the exec target
            Plan plan = createObjectPlan();
            
            plan.setTarget( clazz );
            
            
            // Determine the name that will be used
            String target = null;
            if ( execCommand.name().length() > 0 ) {
                target = execCommand.name();
            } else {
                target = inflector.underscore( clazz.getSimpleName() );
            }
            plan.setTargetName( target );
            
            // Determine the group that will be used
            String group = null;
            if ( execCommand.group().length() > 0 ) {
                group = execCommand.group();
            } else {
                group = clazz.getPackage().getName();
                group = group.substring( group.lastIndexOf(".") + 1 );
                group = inflector.underscore( group );
            }
            plan.setTargetGroup( group );

            // Create root notation of group:target
            String notation = new StringBuilder( group ).append(":").append( target ).toString();
            
            // If default method is set in the annotation
            if ( execCommand.defaultCommand().length() > 0 ) {
                
                Method[] methods = null;
                try {
                    methods = clazz.getMethods();
                } catch ( Error e ) {
                    logger.warn( "Failed to load methods for {}, skipping", clazz.getName(), e );
                    continue;
                }
                
                // Find the method set as the default
                String command = execCommand.defaultCommand();
                for( Method method : methods ) {
                    
                    // Found the method, now set it as the default
                    if ( method.getName().equals( command ) ) {
                        CommandMethod defaultMethod = new CommandMethod( inflector.underscore( command ), method.getName() );
                        defaultMethod.setExit( execCommand.defaultExit() );
                        plan.setDefaultCommand( defaultMethod );
                        
                        commands.put( notation, execCommandDependency );
                    }
                }
                
                // Default method not found, toss exception
                if ( plan.getDefaultCommand() == null ) {
                    throw new CommandException( "Default method "  + command + " not found for " + clazz );
                }
            }
            
            // Find methods annotated with @Command
            for( Method method : clazz.getMethods() ) {
                Command exec = (Command)method.getAnnotation( Command.class );
                if ( exec != null ) {
                    
                    // Determine name for @Command
                    String command = null;
                    if ( exec.name().length() > 0 ) {
                        command = exec.name();
                    } else {
                        command = inflector.underscore( method.getName() );
                    }
                    
                    
                    // Create CommandMethod for @Command
                    CommandMethod commandMethod = new CommandMethod( command, method.getName() );
                    commandMethod.setExit( exec.exit() );
                    commandMethod.setLogResult( exec.logResult() );
                    
                    CommandParam[] commandParams = null;
                    CommandParams params = method.getAnnotation( CommandParams.class );
                    if ( params != null ) {
                        commandParams = params.value();
                    } else {
                        CommandParam param = method.getAnnotation( CommandParam.class );
                        if ( param != null ) {
                            commandParams = new CommandParam[] { param };
                        }
                    }
                    
                    // Set params if set by @Command
                    if ( commandParams != null && commandParams.length > 0) {
                        logger.debug( "CommandParams: {}", commandParams );
                        for ( CommandParam commandParam : commandParams ) {
                            if ( commandParam.defaultValues().length > 0 ) {
                                commandMethod.addParam( commandParam.name(), commandParam.type(), commandParam.defaultValues() );
                                
                            } else {
                                commandMethod.addParam( commandParam.name(), commandParam.type() );
                            }
                        }
                    }
                    
                    // Add the CommandMethod to the Plan for this class
                    plan.addCommand( commandMethod );
                    
                    // Create full notation of group:name:command
                    String commandNotation = new StringBuilder(notation).append(":").append(command).toString();
                    commands.put(commandNotation, execCommandDependency );
                    logger.debug( "Registering command {} {}", commandNotation, execCommandDependency );
                }
            }
            
            // Add the Plan for the Class into the Map registry
            plans.put( clazz, plan );

            // Add the plan description into the commandsDesc helper
            commandsDesc.addAll( plan.commandsDesc() );
            
            // If the class is *not* excluded from group execution, add to group execution
            ByYourCommandGroup execGroup = clazz.getPackage().getAnnotation( ByYourCommandGroup.class );            
            if ( execGroup != null ) {                                
                List<String> excludes = Arrays.asList( execGroup.excludes() );                        
                if ( !excludes.contains( target ) ) {
                    Set<String> groupCommands = groups.get( group );
                    if ( groupCommands == null ) {
                        groupCommands = new HashSet<String>();
                    }
                    groupCommands.add( notation );
                    groups.put( group, groupCommands );
                    
                    logger.debug( "Registering to group {}", group );
                } else {
                    logger.debug( "Excluded from group {}", group );
                }
            }            
        }
    }

    protected CommandDependency newCommandDependency(Class clazz) {
        return new CommandDependency();
    }

    /**
     * Execute a command using the String notation group:target:command
     * 
     * @param commandNotation String
     * @return {@link CommandMethod}
     * @throws CommandException fails to exec
     */
    public CommandMethod exec( String commandNotation ) throws CommandException {
        return exec(commandNotation, null );
    }
    
    /**
     * Execute a command using the String notation group:target:command with
     * a List of params
     * 
     * @param commandNotation String
     * @param params List
     * @return {@link CommandMethod}
     * @throws CommandException fails to exec
     */
    public CommandMethod exec(String commandNotation, List<Object> params ) throws CommandException {
        logger.debug( "execing {}", commandNotation );
        
        String[] notation = commandNotation.split(":");
        
        // execute group
        if ( notation.length == 1 ) {
            logger.info( "execing commands for group {} : {}", commandNotation, groups.get( commandNotation ) );
            CommandMethod commandMethod = null;
            for ( String command : groups.get( commandNotation ) ) {
                commandMethod = exec( command, params );
            }
            
            // XXX: returns last command run in the group!
            return commandMethod;
            
        // execute command
        } if ( notation.length <= 3 ) {
            CommandDependency deps = commands.get(commandNotation);
            Plan plan = getPlan(deps.getTarget());

            if ( plan != null ) {
                return plan.exec( commandNotation, params, this.dependencyResolver );
                
            } else {
                throw new CommandException( "No Exec mapping for " + commandNotation );
            }
            
        // to big
        } else {
            throw new CommandException( "Malformed command: " + commandNotation );
        }
    }
    
    /**
     * Exec the Class' default command
     *
     * @param clazz Class
     * @throws CommandException fails to exec default command
     */
    public void execDefault( Class clazz ) throws CommandException {
        Plan plan = getPlan( clazz );
        if ( plan != null ) {
            plan.exec(null, this.dependencyResolver);
            
        } else {
            throw new CommandException( "Not Exec mapping for class " + clazz );
        }
    }

    /**
     * Exec a group of commands
     * 
     * @param group String
     * @throws CommandException fails to exec group of commands
     */
    public void execGroup( String group ) throws CommandException {
        logger.debug( "execing group {}", group );
        
        for ( String command : groups.get( group ) ) {
            logger.info( "Running command: {}", command );
            exec( command );
        }
    }
    
    /**
     * Register a {@link DependencyResolvable} used to create instances of Command's
     * Class. After constructing a new instance, {@link DependencyResolvable#init}
     * is called.
     * 
     * @param dependencyManager {@link DependencyResolvable}
     * @throws CommandException fail to register a dependency resolver;
     */
    public void registerDependencyResolver(String dependencyManager) throws CommandException {
        Class dependencyResolverClass = null;
                
        try {
            dependencyResolverClass = Class.forName( dependencyManager );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find DependencyManager class: " + dependencyResolverClass, e);
        }                    
        
        DependencyResolvable depResolver = null;
        try {
            depResolver = (DependencyResolvable) ConstructorUtils.invokeConstructor(dependencyResolverClass, null);
        } catch (NoSuchMethodException e) {
            throw new CommandException(e);
        } catch (IllegalAccessException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        } catch (InstantiationException e) {
            throw new CommandException(e);
        }
        
        this.dependencyResolver = depResolver;
    }
    
    /**
     * Get {@link DependencyResolvable} used to create instances of Command's Class
     * 
     * @return {@link DependencyResolvable}
     */
    public DependencyResolvable getDependencyResolver() {
        return dependencyResolver;
    }
    
    /**
     * Get Map of {@link Plan} for a Class
     * 
     * @return Map
     */
    public Map<Class, Plan> getPlans() {
        return plans;
    }
    
    /**
     * Get {@link Plan} for a group:target:name command notation
     * 
     * @param notation String
     * @return {@link Plan}
     */
    public Plan getPlan( String notation ) {
        CommandDependency dep = commands.get( notation );
        if (dep != null ) {
            return getPlan( dep.getTarget() );
        } else {
            return null;
        }
    }
    
    /**
     * Get {@link Plan} for a Class
     * 
     * @param clazz Class
     * @return {@link Plan}
     */
    public Plan getPlan( Class clazz ) {
        return plans.get( clazz );
    }
    
    /**
     * Get Set of all commands registered
     * 
     * @return SEt
     */
    public Set<String> getCommandsDesc() {
        return commandsDesc;
    }

    /**
     * Get Map of all groups registered
     * 
     * @return Map
     */
    public Map<String, Set<String>> getGroups() {
        return groups;
    }
    
    /**
     * Get Map for all commands registered
     * 
     * @return Map
     */
    public Map<String, CommandDependency> getCommands() {
        return commands;
    }

}
