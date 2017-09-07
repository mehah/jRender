package greencode.kernel;

import java.util.Properties;

import greencode.jscript.DOM;
import greencode.jscript.dom.Form;
import greencode.jscript.dom.Window;

public final class $GreenContext {
	private $GreenContext() {}
	
	public static String getProjectContentPath() { return Core.PROJECT_CONTENT_PATH; }	
	
	public static boolean flushed(GreenContext context) { return context.flushed; }	
	public static void flushed(GreenContext context, boolean flushed) { context.flushed = flushed; }	
	public static boolean isForcingSynchronization(GreenContext context, final DOM dom, String name) { return context.isForcingSynchronization(dom, name); }	
	public static void setCurrentWindow(GreenContext context, Window window) { context.currentWindow = window; }
	public static Form getRequestedForm(GreenContext context) { return context.requestedForm; }
	public static String getContextPath() {return Core.CONTEXT_PATH; };
	public static boolean isImmediateSync(GreenContext context) {
		return context.immediateSync;
	}
	
	public static Properties getCurrentMessagePropertie(GreenContext context) {
		return context.currentMessagePropertie;
	}	
}
