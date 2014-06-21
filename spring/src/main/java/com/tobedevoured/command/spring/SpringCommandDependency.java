package com.tobedevoured.command.spring;

import com.tobedevoured.command.CommandDependency;

import java.util.HashSet;
import java.util.Set;

/**
 * The Dependencies required to execute a {@link CommandMethod}
 *
 * Contains functionality for unimplemented Spring support
 *
 * @author Michael Guymon
 */
public class SpringCommandDependency extends CommandDependency {

    Set<String> contexts = new HashSet<String>();
    String beanName;

    /**
     * Get the {@link Set<String>} of resource paths to Spring contexts
     *
     * @return {@link Set<String>}
     */
    public Set<String> getContexts() {
        return contexts;
    }

    /**
     * Set the {@link Set<String>} of resource paths to Spring contexts
     *
     * @param contexts {@link Set<String>}
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
