package com.tobedevoured.command;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Run @ByYourCommand annotated classes, as a GUI or command line.
 * 
 * @author Michael Guymon
 */
public class Runner {
	
	public static final String COMMAND_PATTERN = "(.+)\\[(.+)\\]$";
	
	private static Logger logger = LoggerFactory.getLogger(Runner.class);
	private ByYourCommandManager manager;
	
	public static boolean ALLOW_SYSTEM_EXIT = true;
	public static boolean HAS_ERRORS = false;
	
	/**
	 * Construct new instance
	 * 
	 * @throws CommandException
	 * @throws IOException 
	 * @throws ConfigException 
	 */
	public Runner() throws RunException {
		
		Config config = ConfigFactory.load();
		
		List<String> packages = null;
		Object ref = config.getAnyRef("command.packages");
		if ( ref instanceof List ) {
			packages = (List<String>)ref;
		} else if ( ref instanceof String ) {
			packages = new ArrayList<String>();
			packages.add( (String)ref );
		} else {
			throw new RunException( "command.packages is an invalid format" );
		}
		
		manager = new ByYourCommandManager();
    	try {
			manager.scanForCommands( packages );
		} catch (CommandException commandException) {
			new RunException( commandException );
		}    	
    }
	
	/**
	 * Shutdown runner
	 */
	public void shutdown() {
		shutdown( false );
	}
	
	public void shutdown(boolean hasError) {
		
		if ( ALLOW_SYSTEM_EXIT ) {
			if ( hasError ) {
				System.exit(1);
			} else {
				System.exit(0);
			}		
		}
	}
	
	/**
	 * Get {@link Set<String>} of resouce paths for {@link ApplicationContext}
	 * 
	 * @param commandNotation String
	 * @return Set<String>
	 * @throws CommandException
	 */
	/*
	public Set<String> getCommandContexts( String commandNotation ) throws CommandException {
		return this.manager.getCommandContexts( commandNotation );
	}
	*/
	
	/**
	 * Get list of all commands
	 * 
	 * @return Set<String>
	 */
	public Set<String> getCommandsDesc() {
		return manager.getCommandsDesc();
	}
	
	/**
	 * Get list of all groups
	 * 
	 * @return Set<String>
	 */
	public Set<String> getGroups() {
		return manager.getGroups().keySet();
	}
	
	/**
	 * Get {@link SpringPlan} for a Class
	 * 
	 * @param clazz Class
	 * @return {@link SpringPlan}
	 */
	public Planable getPlan( Class clazz ) {
		return manager.getPlan( clazz );
	}
	
	/**
	 * Get a {@link SpringPlan} for a notation
	 * 
	 * @param notation String
	 * @return {@link SpringPlan}
	 */
	public Planable getPlan( String notation ) {
		CommandDependency dep = manager.getCommands().get( notation );
		return getPlan( dep.getTarget() );
	}
	
	/**
	 * Remove the GUI dropdown's param [] text from command
	 * 
	 * @param command notation
	 * @return String
	 */
	private String removeCommandParamText( String notation ) {
		return notation.replaceAll("\\[.+\\]$", "");
	}
	
	/**
	 * Get {@link CommandMethod} for a String group:target or group:target:command
	 * 
	 * @param commandNotation String
	 * @return {@link CommandMethod}
	 */
	public CommandMethod getCommandMethod( String commandNotation ) {		
		String[] notation = commandNotation.split( ":" );
		
		Planable plan = getPlan( commandNotation );
		if ( plan != null ) {
			if ( notation.length  == 3 ) {
				return plan.getCommands().get( notation[2] );
			} else {
				return plan.getDefaultCommand();
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Exec a notation
	 * 
	 * @param notation
	 * @return {@link CommandMethod}
	 * @throws CommandException
	 */
	public CommandMethod exec( String notation ) throws CommandException {
		return exec( notation, null );
	}
	
	/**
	 * Exec a notation
	 * 
	 * @param notation String
	 * @param params {@link List}
	 * @return {@link CommandMethod}
	 * @throws CommandException
	 */
	public CommandMethod exec( String notation, List<Object> params ) throws CommandException {
		
 	    logger.debug( "Executing {}", notation );
 	    
		CommandMethod commandMethod = manager.exec( notation, params );
		
		return commandMethod;
	}
	
	/**
	 * Exec default for a Class
	 * 
	 * @param clazz Class
	 * @throws CommandException
	 */
	public void execDefault( Class clazz ) throws CommandException {
		manager.execDefault( clazz );
	}
	
	/**
	 * Run GUI Runner
	 * 
	 * @param additionalContexts {@link Set<String>}
	 * @throws CommandException
	 * @throws ConfigException 
	 * @throws IOException 
	 */
	public static void gui() throws RunException {
		
		final Runner runner = new Runner();
		
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		
		final JFrame frame = new JFrame("By Your Command");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(325,115);
		frame.setLocationRelativeTo( null ); // center on screen
		
		
		final JPanel topPanel = new JPanel();
		
		final JPanel middlePanel = new JPanel();
		middlePanel.setVisible( false );
		
		List<String> desc = new ArrayList<String>();
		desc.add( "" );
		desc.addAll( runner.getCommandsDesc() );
		
		final JComboBox commandsCombo = new JComboBox( desc.toArray() );
		commandsCombo.addItemListener( new ItemListener(){
			  public void itemStateChanged(ItemEvent ie){
				  String command = runner.removeCommandParamText((String)ie.getItem());
				  if ( command != null && command.length() > 0 ) {
					  CommandMethod method = runner.getCommandMethod( command );
					  
					  logger.debug( "command: {} method: {} ", command, method );
					  
					  if ( method != null && method.hasParams() ) {
						  middlePanel.removeAll();
						  for ( String name : method.getParamTypes().keySet() ) {
							  JLabel textLabel = new JLabel( name );
							  middlePanel.add( textLabel );
							  
							  Object defaultValue = method.getDefaults().get( name );
							  if ( defaultValue == null || ((String[])defaultValue).length == 1 ) {
							    
								  JTextField text = new JTextField( 30 );						  
								  text.setText( ((String[])defaultValue)[0] );
								  middlePanel.add( text );
								  
							  // If not a String, then can only be a String[]
							  } else {
								  JComboBox params = new JComboBox( (String[])defaultValue );
								  middlePanel.add( params );
							  }						  						 
						  }
						  middlePanel.setVisible( true );					  
						  frame.pack();							  
					  } else {
						  middlePanel.setVisible( false );
						  frame.pack();	
					  }
				  }  
			  }
		 });	
		
		List<String> groups = new ArrayList<String>();
		groups.add( "" );
		groups.addAll( runner.getGroups() );
		
		final JComboBox groupsCombo = new JComboBox( groups.toArray() );		
		
		JComboBox commandTypesCombo = new JComboBox( new String[] { "Command", "Group" } );
		commandTypesCombo.addItemListener( new ItemListener(){
			  public void itemStateChanged(ItemEvent ie){				  
				  String type = (String)ie.getItem();				  
				  if ( "Command".equals( type ) ) {
					  topPanel.remove( 1 );
					  topPanel.add( commandsCombo );					  
				  } else if ( "Group".equals( type ) ) {
					  topPanel.remove(1);
					  topPanel.add( groupsCombo );
				  }
				  
				  frame.pack();
			  }
		 });
		
		topPanel.add( commandTypesCombo );
		topPanel.add( commandsCombo);
		
		JButton saveButton = new JButton( "Run" );
		frame.getRootPane().setDefaultButton( saveButton );
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				List<Object> params = new ArrayList<Object>();
				for ( Component component : middlePanel.getComponents() ) {
					if ( component instanceof JTextField ) {
						params.add( ((JTextField)component).getText() );
					} else if ( component instanceof JComboBox ) {
						params.add( (String)((JComboBox)component).getSelectedItem() );
					}
				}
				frame.dispose();
				
				String command = (String)((JComboBox)topPanel.getComponent(1)).getSelectedItem();
				if ( command != null && command.length() > 0 ) {
					command = runner.removeCommandParamText(command);
					
					CommandMethod commandMethod = null;
					try {
						commandMethod = runner.exec( command, params );					
					} catch (Exception e) {
						logger.error( "Failed to run command: {}", command, e );
					}
					
					if ( commandMethod == null || commandMethod.isExit() ) {
						logger.debug( "Finished command" );
						runner.shutdown();
					}
				} else {
					logger.warn( "Please select a command to run" );
				}
			}
		});
				
		JButton cancelButton = new JButton( "Cancel" );
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				frame.dispose();
			}
		});
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add( saveButton );
		bottomPanel.add( cancelButton );

		frame.add(topPanel, BorderLayout.NORTH );
		frame.add( middlePanel, BorderLayout.CENTER );
		frame.add(bottomPanel, BorderLayout.SOUTH );
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Run Text Runner with commandline args
	 * 
	 * @param args String[]
	 * @throws CommandException
	 */
	public static void text(String[] args) throws RunException {
		Runner runner = new Runner();
		
		if (args == null || args.length == 0 || "--help".equals(args[0]) || "-help".equals(args[0])) {
			System.out.println("");
			System.out.println("Commands: ");
			for (String command : runner.getCommandsDesc()) {
				System.out.println("  " + command);
			}
			System.out.println(" " );
			System.out.println("Groups: ");
			for (String group : runner.getGroups()) {
				System.out.println("  " + group);
			}

		} else {
			Pattern pattern = Pattern.compile(COMMAND_PATTERN);
			
			// Iterate commands first to build list of Spring context dependencies
			for (String command : args) {
				Matcher matcher = pattern.matcher(command);
				
				if ( matcher.matches() ) {
					command = matcher.group(1);
				}
				
			}
			
			// Iterate commands a second time to exec the commands
			CommandMethod commandMethod = null;
			for (String command : args) {
				
				Matcher matcher = pattern.matcher(command);
				
				List<Object> params = new ArrayList<Object>();
				if ( matcher.matches() ) {
					command = matcher.group(1);
					String parameter = matcher.group(2);
					if ( parameter != null ){
						if ( parameter.contains(",") ) {
							for ( String part : parameter.split( ",") ) {
								params.add( part.trim() );
							}
						} else {
							params.add( parameter );
						}
					}
				}
				
				try {
					commandMethod = runner.exec(command, params);
				} catch ( Exception e ) {
					logger.error( "Failed to run command: {} ", command, e );
					HAS_ERRORS = true;
				}
			}
			
			// the last command run determines if runner must shutdown
			if ( commandMethod == null || commandMethod.isExit() ) {
				if ( HAS_ERRORS ) {
					logger.warn( "Finished command with an Error" );
				} else {
					logger.debug( "Finished command" );
				}
				runner.shutdown( HAS_ERRORS );
			}
		}	
	}
	
	/**
	 * Run Runner, will run {@link #gui} if not {@link GraphicEnvironment#isHeadLess}, otherwise
	 * runs a {@link #text}
	 * 
	 * @param args String[]
	 * @throws CommandException
	 */
	public static void run(String[] args) throws RunException {
		
		boolean headlessCheck = GraphicsEnvironment.isHeadless();
		if ( headlessCheck ) {
			text( args );
		} else {
			// Always run as text if args are set
			if ( args != null && args.length > 0 ) {
				text( args );
			} else {
				gui();
			}
		}
	}
	
	/**
	 * Alias for {@link #run(String[])}
	 * 
	 * @param args String[] args
	 * @throws RunException
	 */
	public static void main(String[] args) throws RunException {
		Runner.run(args);
	}
}
