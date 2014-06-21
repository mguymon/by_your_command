package com.tobedevoured.command;

import org.apache.commons.beanutils.ConstructorUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class DefaultDependencyResolver implements DependencyResolvable {
    protected ByYourCommandManager manager;

    public void init(Map<String, List> commandsToRun) {
        // NOOP
    }

    public <T> T getInstance(Class<T> clazz) throws CommandException {
        try {
            return (T)ConstructorUtils.invokeConstructor(clazz, null);
        } catch (NoSuchMethodException e) {
            throw new CommandException(e);
        } catch (IllegalAccessException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        } catch (InstantiationException e) {
            throw new CommandException(e);
        }
    }

    public void setManager(ByYourCommandManager manager) {
        this.manager = manager;
    }
}
