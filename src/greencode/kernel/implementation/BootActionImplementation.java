package greencode.kernel.implementation;

import greencode.kernel.GreenContext;

import java.lang.reflect.Method;


public abstract interface BootActionImplementation extends PluginImplementation {
	public void initUserContext(GreenContext context);
	public void onRequest();
	public boolean beforeAction(GreenContext context, Method requestMethod);
	public void afterAction(GreenContext context, Method requestMethod);
}
