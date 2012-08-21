package com.tobedevoured.command;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;

public class ConfigTest {

	
	@Test
	public void getPackages() throws IOException {
		assertEquals( Arrays.asList( "com.tobedevoured.command" ), ConfigFactory.load("test").getAnyRefList("command.packages") );
	}
}
