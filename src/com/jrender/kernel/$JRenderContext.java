package com.jrender.kernel;

import java.util.Properties;

import com.jrender.jscript.DOM;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.Window;

public final class $JRenderContext {
	private $JRenderContext() {}
	
	public static String getProjectContentPath() { return Core.PROJECT_CONTENT_PATH; }	
	
	public static boolean flushed(JRenderContext context) { return context.flushed; }	
	public static void flushed(JRenderContext context, boolean flushed) { context.flushed = flushed; }	
	public static boolean isForcingSynchronization(JRenderContext context, final DOM dom, String name) { return context.isForcingSynchronization(dom, name); }	
	public static void setCurrentWindow(JRenderContext context, Window window) { context.currentWindow = window; }
	public static Form getRequestedForm(JRenderContext context) { return context.requestedForm; }
	public static String getContextPath() {return Core.CONTEXT_PATH; };
	public static boolean isImmediateSync(JRenderContext context) {
		return context.immediateSync;
	}
	
	public static Properties getCurrentMessagePropertie(JRenderContext context) {
		return context.currentMessagePropertie;
	}	
}
