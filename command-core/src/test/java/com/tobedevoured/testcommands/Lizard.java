package com.tobedevoured.testcommands;

import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.annotation.Command;

@ByYourCommand
public class Lizard {

	@Command
	public void crawl() {
		System.out.println( "inch by inch" );
	}
	
	@Command
	public void lazy() {
		System.out.println( "very" );
	}
	
	@Command
	public void lick() {
		System.out.println( "lazy lizards are unlikely to lick" );
	}
}
