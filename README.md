# By Your Command

By Your Command is simple way to make Java do your bidding. It provides an easy
way to turn any Java Class into executable that can be run directly or from a jar.

https://github.com/mguymon/by_your_command

## Install

Dependency for Maven can use

    <dependency>
      <groupId>com.tobedevoured.command</groupId>
      <artifactId>core</artifactId>
      <version>0.0.1</version>
    </dependency>

## Config

Create a [Typesafe Config](https://github.com/typesafehub/config) _application.conf_ and set the packages to be scanned for commands

    command.packages=com.package.to.scan

or

    command.packages=[com.package.to.scan1, com.package.to.scan2]

## How Does it Work

By scanning the classpath for Classes annotated with _@ByYourCommand_, methods are made executable by a GUI or from the command line via the **com.tobedevoured.command.Runner**.

Screenshot of the GUI Runner

![Example GUI](https://raw.github.com/mguymon/by_your_command/master/gui_example.png)

Screenshot of the Text Runner

![Example CLI](https://raw.github.com/mguymon/by_your_command/master/cli_example.png)

### Example @ByYourCommand Java

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

#### Cavaet

_ByYourCommand_ presently only works with methods with no parameters or _String_ for _@CommandParam_.

### Executing a Command

In _main(String[] args)_ of the Class, execute _com.tobedevoured.command.Runner.run( args )_ and ByYourCommand will scan the classpath and start the Runner to GUI or if there is no video support, command line.

To create an executable Jar, set the _MANIFEST.MF_ with

     Main-Class: com.tobedevoured.command.Runner

## License

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with this
work for additional information regarding copyright ownership.  The ASF
licenses this file to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
