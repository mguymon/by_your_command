package com.tobedevoured.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Helper for changing the logging levels at runtime.
 * 
 * @author Michael Guymon
 */
public class LogUtil {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger( LogUtil.class );
	
	/**
	 * Change log {@link Level} to only output error logging
	 * 
	 * @return {@link Level}
	 */
	public static Level errorOnlyLogging() {
		// Disable logging while Spring loads    
	 	final Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	 	final Level originalLevel = currentRootLevel();
	 	root.setLevel(Level.ERROR);
	 	
	 	return originalLevel;
	}
	
	/**
	 * Change the log {@link Level} for root logger.
	 * 
	 * @param level {@link Level}
	 */
	public static void changeRootLevel( final Level level ) {
		changeLevel(Logger.ROOT_LOGGER_NAME, level );
	}
	
	/**
	 * Change the log {@link Level} for a logger
	 * 
	 * @param logger String 
	 * @param level {@link Level}
	 */
	public static void changeLevel( final String logger, final Level level ) {
		final Logger log = (Logger)LoggerFactory.getLogger(logger);
	 	log.setLevel( level );
	}
	
	public static void changeLevel( final String logger, final String level ) {
		Level logLevel = null;
		
		if ( "debug".equalsIgnoreCase( level ) ) {
			logLevel = Level.DEBUG;
		} else if ( "warn".equalsIgnoreCase( level ) ) {
			logLevel = Level.WARN;
		} else if ( "error".equalsIgnoreCase( level ) ) {
			logLevel = Level.ERROR;
		} else {
			logLevel = Level.INFO;
		}
		
		final Logger log = (Logger)LoggerFactory.getLogger(logger);
	 	log.setLevel( logLevel );
	}
	
	/**
	 * Get the current {@link Level} for the root logger
	 * @return {@link Level}
	 */
	public static Level currentRootLevel() {
		final Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		return root.getLevel();
	}
	
	public static void configureFromFile(String path) throws IOException, JoranException {
		configureFromFile( new File( path ) );
	}
	
	/**
	 * Load a logback.xml from the local file system. The configuration is merged with
	 * the logback.xml from the classpath.
	 * 
	 * @param config {@link File}
	 * @throws IOException
	 * @throws JoranException
	 */
	public static void configureFromFile(File config) throws IOException, JoranException {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		if(!config.exists()){
			throw new IOException( "Logback config file " + config.getPath() + " does not exists");
		}
		
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(loggerContext);
		
		InputStream input = LogUtil.class.getClassLoader().getResourceAsStream( "logback.xml" );
		configurator.doConfigure(input);
		configurator.doConfigure(config);
		
		input.close();

		logger.info("Configured Logback from: " + config.getAbsolutePath() );
	}
	
	
}
