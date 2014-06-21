package com.tobedevoured.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByYourCommand {
	String name() default "";
	String defaultCommand() default "";
	boolean defaultExit() default true;
	String group() default "";
	
}
