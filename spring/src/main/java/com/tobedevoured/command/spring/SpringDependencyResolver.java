package com.tobedevoured.command.spring;

import com.tobedevoured.command.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class SpringDependencyResolver extends DefaultDependencyResolver {
    ApplicationContext springContext;

    Map<Class, String> beanLookUp = new HashMap<Class,String>();

    public void init(Map<String, List> commandsToRun) {
        Set<String> contexts = new HashSet<String>();

        for ( String command : commandsToRun.keySet() ) {
            SpringCommandDependency dep = (SpringCommandDependency)manager.getCommands().get(command);
            if (dep != null) {
                contexts.addAll( dep.getContexts() );

                if (StringUtils.isNotBlank( dep.getBeanName() ) ) {
                    beanLookUp.put( dep.getTarget(), dep.getBeanName() );
                }
            }
        }

        loadSpringContext(contexts);
    }

    public void loadSpringContext(Set<String> contexts) {
        springContext = new ClassPathXmlApplicationContext(contexts.toArray(new String[contexts.size()]));
    }

    public <T> T getInstance(Class<T> clazz) throws CommandException {
        String beanName = beanLookUp.get(clazz);

        try {
            if ( StringUtils.isNotBlank(beanName) ) {
                return (T)springContext.getBean(beanName);
            } else {
                return springContext.getBean(clazz);
            }
        } catch( NoSuchBeanDefinitionException e ) {

            // Class not directly registered with Spring, manually construct it
            return  super.getInstance(clazz);
        }
    }

}
