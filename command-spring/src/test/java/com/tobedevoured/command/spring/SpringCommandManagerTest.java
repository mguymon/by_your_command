package com.tobedevoured.command.spring;


import com.tobedevoured.command.CommandException;
import com.tobedevoured.command.Plan;
import com.tobedevoured.command.RunException;
import com.tobedevoured.command.spring.testcommands.Hamster;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;

public class SpringCommandManagerTest {
    private SpringCommandManager manager;

    @Before
    public void setup() throws Exception {
        manager = new SpringCommandManager();
    }

    @Test
    public void springInjection() throws CommandException {
        manager.scanForCommands( "com.tobedevoured.command.spring.testcommands" );

        Map<String,List> commandsToRun = new HashMap<String,List>();
        commandsToRun.put("testcommands:hamster:eat", new ArrayList());

        manager.getDependencyResolver().init(commandsToRun);

        Hamster hamster = manager.getDependencyResolver().getInstance(Hamster.class);
        assertNotNull(hamster);
        assertNotNull(hamster.getWheel());
    }

    public static void main(String[] args) throws RunException {
        SpringRunner.run(args);
    }
}
