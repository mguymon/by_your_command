package com.tobedevoured.command.spring;

import com.tobedevoured.command.ByYourCommandManager;
import com.tobedevoured.command.CommandDependency;
import com.tobedevoured.command.DefaultDependencyResolver;
import com.tobedevoured.command.DependencyResolvable;
import com.tobedevoured.command.spring.annotation.SpringSupport;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Extends {@link ByYourCommandManager} to add support for {@link SpringSupport} annotations.
 */
public class SpringCommandManager extends ByYourCommandManager {

    /**
     * Create new {@link SpringCommandDependency} for a Class
     *
     * @param clazz {@link Class}
     * @return {@link CommandDependency}
     */
    @Override
    protected CommandDependency newCommandDependency(Class clazz) {
        SpringCommandDependency dep = new SpringCommandDependency();
        SpringSupport springSupport = (SpringSupport)clazz.getAnnotation( SpringSupport.class );
        if ( springSupport != null ) {
            String[] contexts = springSupport.contexts();
            dep.setContexts( new HashSet<String>( Arrays.asList(contexts) ) );


            if ( StringUtils.isNotBlank( springSupport.beanName() ) ) {
                dep.setBeanName( springSupport.beanName() );
            }
        }
        return dep;
    }

    /**
     * Override to return a new instance of {@link SpringDependencyResolver}
     *
     * @return {@link SpringDependencyResolver}
     */
    @Override
    public DependencyResolvable buildDependencyResolver() {
        return new SpringDependencyResolver();
    }
}
