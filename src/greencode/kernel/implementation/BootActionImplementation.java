package greencode.kernel.implementation;

import greencode.kernel.GreenContext;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface BootActionImplementation extends PluginImplementation {
	public boolean beforeAction(GreenContext context, Method requestMethod);

	public void afterAction(GreenContext context, Method requestMethod);

	public void onRequest(HttpServletRequest request,
			HttpServletResponse response);

	public void initUserContext(GreenContext context);
}
