package com.jrender.kernel.implementation;

import javax.servlet.ServletContext;

import com.jrender.kernel.CoreFileJS;

public interface PluginImplementation {
	public void destroy();
	public void init(final String projectName, ClassLoader classLoader, final ServletContext context, final CoreFileJS coreFileJS);
}
