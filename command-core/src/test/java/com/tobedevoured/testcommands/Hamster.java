package com.tobedevoured.testcommands;

import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;
import com.tobedevoured.command.annotation.CommandParam;

@ByYourCommand
public class Hamster {
	
	@Command
	public void eat() {
		System.out.println( "Yum" );
	}
	
	@Command
	public void sleep() {
		System.out.println( "Zzzzzz" ); 
	}
	
	@Command
	@CommandParam(name = "with", type = String.class)
	public void play( String with ) {
		System.out.println( with + " is fun" ); 
	}
	
	public static void main(String[] args) throws RunException {
		Runner.run( args );
	}
}
