package com.tobedevoured.command;

public interface DependencyManagable {

    void init();
    
    <T> T getTarget(Class<T> clazz) throws CommandException;
}
