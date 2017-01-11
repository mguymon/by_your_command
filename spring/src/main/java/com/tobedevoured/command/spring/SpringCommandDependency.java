package com.tobedevoured.command.spring;

import com.tobedevoured.command.CommandDependency;

import java.util.HashSet;
import java.util.Set;

/**
 * The Dependencies required to execute a CommandMethod
 *
 * @author Michael Guymon
 */
public class SpringCommandDependency extends CommandDependency {

    Set<String> contexts = new HashSet<String>();
    String beanName;

    /**
     * Get the Set of resource paths to Spring contexts
     *
     * @return Set
     */
    public Set<String> getContexts() {
        return contexts;
    }

    /**
     * Set the Set of resource paths to Spring contexts
     *
     * @param contexts Set
     */
    public void setContexts(Set<String> contexts) {
        this.contexts = contexts;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public  String getBeanName() {
        return beanName;
    }
}
