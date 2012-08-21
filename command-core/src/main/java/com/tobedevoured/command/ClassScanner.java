package com.tobedevoured.command;

import com.metapossum.utils.scanner.reflect.ClassesInPackageScanner;

public class ClassScanner extends ClassesInPackageScanner {

	public ClassScanner() {
		this.classLoader = getDefaultClassLoader();
        this.resourceLoader = new ClassResourceLoader( getDefaultClassLoader(), false );
	}
}
