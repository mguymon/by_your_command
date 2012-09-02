package com.tobedevoured.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.tobedevoured.command.ByYourCommandManager;

public class ByYourCommandManagerTest {
	
	
	private ByYourCommandManager manager;
	
	@Before
	public void setup() throws Exception {
		manager = new ByYourCommandManager();
		manager.scanForCommands( "com.tobedevoured.testcommands" );
	}
	
	@Test
	public void getCommandsDesc() {
		Set<String> descs = manager.getCommandsDesc();
		
		assertEquals( new TreeSet( Arrays.asList( 
			"testcommands:hamster:eat", "testcommands:hamster:sleep", "testcommands:lizard:crawl", 
			"testcommands:lizard:lazy", "testcommands:lizard:lick" ) ), descs );
	}
}
