package com.tobedevoured.command;

public class ClassResourceLoader extends com.metapossum.utils.scanner.reflect.ClassResourceLoader {

	public ClassResourceLoader(ClassLoader classLoader, boolean includeInnerClasses) {
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
