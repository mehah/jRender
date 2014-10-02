package greencode.kernel.implementation;

import greencode.kernel.CoreFileJS;

import javax.servlet.ServletContext;

public interface PluginImplementation {
	public void init(final String projectName, ClassLoader classLoader, final ServletContext context, final CoreFileJS coreFileJS);
	public void destroy();	
}
