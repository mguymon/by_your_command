package com.tobedevoured.command;

import com.metapossum.utils.scanner.reflect.ClassResourceLoader;

/**
 * Scans the Classpath for {@link ByYourCommand} anntotated Classes
 * 
 * @author Michael Guymon
 *
 */
public class CommandResourceLoader extends ClassResourceLoader {

	public CommandResourceLoader(ClassLoader classLoader, boolean includeInnerClasses) {
		super(classLoader, includeInnerClasses);
		
	}

	protected Class<?> loadClassFromFile(String packageName, String fileName) {
		try {
			return super.loadClassFromFile(packageName, fileName);
		} catch ( Throwable throwable ) {
			return null;
		}
	}
}
