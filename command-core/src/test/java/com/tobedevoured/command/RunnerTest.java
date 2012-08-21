package com.tobedevoured.command;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tobedevoured.command.Runner;


public class RunnerTest {

	
	@Test
	public void parseCommandLineParams() {
		Pattern pattern = Pattern.compile(Runner.COMMAND_PATTERN);
		Matcher matcher = pattern.matcher("test1:test2[param]");
		assertTrue( "should match for param", matcher.matches() );
		assertEquals( "test1:test2", matcher.group(1) );
		assertEquals( "param", matcher.group(2) );
	}
	
}
