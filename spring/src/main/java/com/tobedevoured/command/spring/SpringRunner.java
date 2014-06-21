package com.tobedevoured.command.spring;

import com.tobedevoured.command.RunException;
import com.tobedevoured.command.Runner;

/**
 * Extends {@link Runner} to use {@link SpringCommandManager}
 */
public class SpringRunner extends Runner {

    public SpringRunner(String _package) throws RunException {
        super(_package);
    }

    public SpringRunner() throws RunException {
        super();
    }

    @Override
    protected SpringCommandManager createCommandManager() {
        return new SpringCommandManager();
    }

    /**
     * Alias for {@link #run(String[])}
     *
     * @param args String[] args
     * @throws RunException
     */
    public static void main(String[] args) throws RunException {
        ( new SpringRunner() ).run(args);
    }
}
