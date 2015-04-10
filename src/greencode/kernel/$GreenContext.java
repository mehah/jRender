package greencode.kernel;

import greencode.jscript.Form;
import greencode.jscript.Window;

public final class $GreenContext {
	private $GreenContext() {}
	
	public static boolean flushed(GreenContext context) { return context.flushed; }	
	public static void flushed(GreenContext context, boolean flushed) { context.flushed = flushed; }	
	public static boolean isForcingSynchronization(GreenContext context, String name) { return context.isForcingSynchronization(name); }	
	public static void setCurrentWindow(GreenContext context, Window window) { context.currentWindow = window; }
	public static Form getRequestedForm(GreenContext context) { return context.requestedForm; }
	public static String getContextPath() {return Core.CONTEXT_PATH; };
}
