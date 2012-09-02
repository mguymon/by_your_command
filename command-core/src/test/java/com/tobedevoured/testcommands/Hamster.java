package com.tobedevoured.testcommands;

import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;
import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;

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
	
	public static void main(String[] args) throws RunException {
		Runner.run( args );
	}
}
