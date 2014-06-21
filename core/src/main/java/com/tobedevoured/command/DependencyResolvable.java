package com.tobedevoured.command;

import java.util.List;
import java.util.Map;

public interface DependencyResolvable {

    void init(Map<String, List> commandsToRun);
    
    <T> T getInstance(Class<T> clazz) throws CommandException;

    void setManager(ByYourCommandManager manager);
}
