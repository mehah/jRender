package greencode.kernel;

import greencode.exception.StopProcess;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class Console {	
	public static void error(Throwable e) {
		if(e.getClass().equals(StopProcess.class))
			return;
		
		GreenContext context = GreenContext.getInstance();
		e.printStackTrace();
		if(context != null && context.getResponse() != null) {
			JsonArray stackTrace = new JsonArray();
			JsonObject json = new JsonObject();
			json.add("stackTrace", stackTrace);
			
			json.addProperty("className", e.getClass().getName());
			json.addProperty("message", e.getMessage());
			
			for (StackTraceElement trace : e.getStackTrace()) {
				if(trace == null)
					continue;
				
				JsonObject o = new JsonObject();
				o.addProperty("className", trace.getClassName());
				o.addProperty("methodName", trace.getMethodName());
				o.addProperty("lineNumber", trace.getLineNumber());
				o.addProperty("fileName", trace.getFileName());
				try {
					ClassLoader cl = Class.forName(trace.getClassName()).getClassLoader();
					if(cl == null)
						throw new ClassNotFoundException();
					
					o.addProperty("possibleError", cl.getResource("/") != null);
				} catch (ClassNotFoundException e1) {
					o.addProperty("possibleError", false);
				}
				stackTrace.add(o);
			}
			
			addErro(json);
		}
		
		throw new StopProcess();
	}
	
	public static void error(String msg) {
		System.err.println("\n["+Core.projectName+":Error]: "+msg);
		
		GreenContext context = GreenContext.getInstance();
		
		if(context != null && context.getResponse() != null) {
			JsonObject json = new JsonObject();			
			json.addProperty("message", msg);
			addErro(json);
		}
		
		throw new StopProcess();
	}
	
	public static void warning(String msg) { System.err.println("["+Core.projectName+":Warning]: "+msg); }
	
	public static void log(String msg) {
		if(GreenCodeConfig.Server.writeLog)
			System.out.println("["+Core.projectName+"] "+msg);
	}
	
	static void addErro(JsonObject o) { GreenContext.getInstance().errors.add(o); }
}
