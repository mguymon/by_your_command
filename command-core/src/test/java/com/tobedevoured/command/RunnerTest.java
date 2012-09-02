package com.tobedevoured.command;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;

import com.tobedevoured.command.Runner;


public class RunnerTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	@Before
	public void setupRunner() {
		Runner.ALLOW_SYSTEM_EXIT = false;
	}
	
	@Before
	public void setUpStreams() {
	   System.setOut(new PrintStream(outContent));
	   System.setErr(new PrintStream(errContent));
	}
	
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}
	
	@Test
	public void parseCommandLineParams() {
		Pattern pattern = Pattern.compile(Runner.COMMAND_PATTERN);
		Matcher matcher = pattern.matcher("test1:test2[param]");
		assertTrue( "should match for param", matcher.matches() );
		assertEquals( "test1:test2", matcher.group(1) );
		assertEquals( "param", matcher.group(2) );
	}
	
	@Test
	public void execText() throws RunException {
		LogUtil.changeRootLevel( Level.ERROR );
		Runner.text( new String[] { "testcommands:hamster:eat" } );
		assertEquals("Yum\n", outContent.toString());
		LogUtil.changeRootLevel( Level.DEBUG );
	}
	
}
