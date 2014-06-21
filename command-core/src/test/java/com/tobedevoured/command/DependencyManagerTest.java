package com.tobedevoured.command;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tobedevoured.testcommands.Hamster;

public class DependencyManagerTest {

    ByYourCommandManager manager;
    
    @Before
    public void setup() {
        manager = new ByYourCommandManager();
    }
    
    @Test
    public void defaultDependencyManager() throws CommandException {
        DependencyResolvable depManager = manager.getDependencyResolver();
        
        Hamster hamster = depManager.getInstance( Hamster.class );
        
        assertTrue("Should be a Hamster", hamster instanceof Hamster );
    }
    
    
    public void registerDependencyManager() throws CommandException {
        DependencyResolvable proxyDepManager = new DependencyResolvable() {
            
            public void init(Map<String, List> commandsToRun) {
                // NOOP
            }

            public <T> T getInstance(Class<T> clazz) throws CommandException {
                InvocationHandler handler = new InvocationHandler() {

                    public Object invoke(Object arg0, Method arg1, Object[] arg2)
                            throws Throwable {
                        
                        return null;
                    }
                    
                };
                
                return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                        new Class[] { clazz },
                        handler);
            }

            public void setManager(ByYourCommandManager manager) {
                // NOOP
            }

        };
        
        manager.registerDependencyResolver(proxyDepManager.getClass().getSimpleName());
        
        DependencyResolvable depManager = manager.getDependencyResolver();
        Hamster hamster = depManager.getInstance( Hamster.class );
        assertTrue("Should be a Hamster", hamster instanceof Hamster );
        assertTrue("Should be a Hamster Proxy", Proxy.isProxyClass( hamster.getClass() ) );
    }
}
