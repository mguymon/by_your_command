package com.tobedevoured.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestHelper {

	public static Set toSet( Object ... args ) {
		return new HashSet( Arrays.asList( args ) );
	}
	
	public static void captureOutput( CaptureTest test ) throws Exception {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		
		test.test( outContent, errContent );
		
		System.setOut(null);
	    System.setErr(null);
		
	}
}


abstract class CaptureTest {
	
	public abstract void test( ByteArrayOutputStream outContent, ByteArrayOutputStream errContent ) throws Exception;
	
}
