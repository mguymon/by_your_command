package com.tobedevoured.command;

import static org.junit.Assert.*;
import static com.tobedevoured.command.TestHelper.*;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;

import com.tobedevoured.command.ByYourCommandManager;
import com.tobedevoured.testcommands.Hamster;
import com.tobedevoured.testcommands.Lizard;

public class ByYourCommandManagerTest {
	
	
	private ByYourCommandManager manager;
	
	@Before
	public void setup() throws Exception {
		manager = new ByYourCommandManager();
	}
	
	@Test
	public void constructor() {
		assertNotNull( manager.getPlans() );
		assertNotNull( manager.getCommands() );
		assertNotNull( manager.getCommandsDesc() );
		assertNotNull( manager.getGroups() );
	}
	
	@Test
	public void createObjectPlan() {
		assertNotNull( manager.createObjectPlan() );
	}
	
	@Test
	public void scanForCommands() throws CommandException {
		manager.scanForCommands( "com.tobedevoured.testcommands" );
		
		assertEquals( toSet( Lizard.class, Hamster.class ), manager.getPlans().keySet() );
		
		Planable plan = manager.getPlans().get( Lizard.class );
		assertEquals( "lazy", plan.getDefaultCommand().getName() );
		assertEquals( "lanky", plan.getTargetGroup() );
		assertEquals( "lizard", plan.getTargetName() );
		assertEquals( toSet( "crawl", "lazy", "lick" ), plan.getCommands().keySet() );
		
		plan = manager.getPlans().get( Hamster.class );
		assertNull( plan.getDefaultCommand() );
		assertEquals( "testcommands", plan.getTargetGroup() );
		assertEquals( "hamster", plan.getTargetName() );
		assertEquals( toSet( "eat", "play", "sleep" ), plan.getCommands().keySet() );
		
		assertEquals( toSet( 
			"lanky:lizard:lick", "testcommands:hamster:eat", "testcommands:hamster:play", "testcommands:hamster:sleep", 
			"lanky:lizard", "lanky:lizard:lazy", "lanky:lizard:crawl" ), manager.getCommands().keySet() );
		
		assertEquals( toSet( "testcommands", "lanky" ), manager.getGroups().keySet() );
		
		Set<String> descs = manager.getCommandsDesc();
		
		assertEquals( toSet( 
				"lanky:lizard", "lanky:lizard:crawl", "lanky:lizard:lazy", "lanky:lizard:lick", 
				"testcommands:hamster:eat", "testcommands:hamster:play[with]", "testcommands:hamster:sleep" ), descs );
	}
	
	@Test
	public void exec() throws Exception {
		manager.scanForCommands( "com.tobedevoured.testcommands" );
		
		captureOutput( new CaptureTest() {
			@Override
			public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
				manager.exec( "lanky:lizard" );
				assertEquals( "very\n", outContent.toString() );
			}
		});
		
		captureOutput( new CaptureTest() {
			@Override
			public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
				manager.exec( "lanky:lizard:lick" );
				assertEquals( "lazy lanky lizards are unlikely to lick\n", outContent.toString() );
			}
		});
	}
	
	@Test
	public void execDefault() throws Exception {
		manager.scanForCommands( "com.tobedevoured.testcommands" );
		
		captureOutput( new CaptureTest() {
			@Override
			public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
				manager.execDefault( Lizard.class );
				assertEquals( "very\n", outContent.toString() );
			}
		});
		
		try {
			captureOutput( new CaptureTest() {
				@Override
				public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
					manager.execDefault( Hamster.class );
					assertEquals( "lazy lanky lizards are unlikely to lick\n", outContent.toString() );
				}
			});
		} catch ( CommandException ex ) {
			assertEquals( "Default method not set for class com.tobedevoured.testcommands.Hamster", ex.getMessage() );
		}
	}
	
}
