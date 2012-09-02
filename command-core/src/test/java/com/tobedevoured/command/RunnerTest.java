package com.tobedevoured.command;

import static org.junit.Assert.*;
import static com.tobedevoured.command.TestHelper.*;

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

	@Before
	public void setupRunner() {
		Runner.ALLOW_SYSTEM_EXIT = false;
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
	public void execTextHelp() throws Exception {
		captureOutput( new CaptureTest() {
			@Override
			public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
				LogUtil.changeRootLevel( Level.ERROR );
				Runner.text( new String[] { "--help" } );
				assertEquals(
					"\nCommands: \n" +
					"  lanky:lizard\n" +
					"  lanky:lizard:crawl\n" +
					"  lanky:lizard:lazy\n" +
					"  lanky:lizard:lick\n" +
					"  testcommands:hamster:eat\n" +
					"  testcommands:hamster:sleep\n" +
					" \n" +
					"Groups: \n" +
					"  testcommands\n" +
					"  lanky\n", outContent.toString());
				LogUtil.changeRootLevel( Level.DEBUG );
			}
			
		});	
	}
	
	@Test
	public void execText() throws Exception {
		captureOutput( new CaptureTest() {
			@Override
			public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
				LogUtil.changeRootLevel( Level.ERROR );
				Runner.text( new String[] { "testcommands:hamster:eat" } );
				assertEquals("Yum\n", outContent.toString());
				LogUtil.changeRootLevel( Level.DEBUG );
			}
			
		});	
		
		captureOutput( new CaptureTest() {
			@Override
			public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
				LogUtil.changeRootLevel( Level.ERROR );
				Runner.text( new String[] { "lanky:lizard:lick" } );
				assertEquals("lazy lanky lizards are unlikely to lick\n", outContent.toString());
				LogUtil.changeRootLevel( Level.DEBUG );
			}
			
		});	
	}
	
}
