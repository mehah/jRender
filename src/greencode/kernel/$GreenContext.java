package greencode.kernel;

import greencode.jscript.Window;

public final class $GreenContext {
	private $GreenContext() {}
	
	public static void executeAction(GreenContext context, boolean value) { context.executeAction = value; }	
	public static boolean flushed(GreenContext context) { return context.flushed; }	
	public static void flushed(GreenContext context, boolean flushed) { context.flushed = flushed; }	
	public static boolean forceSynchronization(GreenContext context) { return context.forceSynchronization; }	
	public static void setCurrentWindow(GreenContext context, Window window) { context.currentWindow = window; }
}
