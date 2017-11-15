package com.jrender.kernel.implementation;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.Window;
import com.jrender.kernel.JRenderContext;
import com.jrender.validator.DataValidation;

public abstract interface BootActionImplementation extends PluginImplementation {
	public boolean beforeAction(JRenderContext context, Method requestMethod);

	public void afterAction(JRenderContext context, Method requestMethod);

	public void beforeValidation(DataValidation dataValidation);

	public void afterValidation(Form form, DataValidation dataValidation);

	public boolean onRequest(HttpServletRequest request, HttpServletResponse response);
	
	public void onException(JRenderContext context, Exception e);

	public void initUserContext(JRenderContext context);
	
	public boolean whenUnauthorized(JRenderContext context);
	
	public void sessionDestroyed(HttpSession session);
	
	public void onRegisteredEventLost(JRenderContext context, Window window);
}
