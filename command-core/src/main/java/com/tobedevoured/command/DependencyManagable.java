package com.tobedevoured.command;

public interface DependencyManagable {

    void init();
    
    <T> T getInstance(Class<T> clazz) throws CommandException;
}
