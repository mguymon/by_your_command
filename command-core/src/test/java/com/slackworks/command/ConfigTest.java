package com.slackworks.command;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

	ConfigFactory config;
	
	@Test
	public void getPackages() throws IOException {
		assertEquals( Arrays.asList( "com.slackworks.command" ), ConfigFactory.load("test").getAnyRefList("command.packages") );
	}
}
