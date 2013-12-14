package greencode.kernel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class Console {
	static ThreadLocal<JsonArray> errors = new ThreadLocal<JsonArray>();
	
	public static void error(Throwable e)
	{
		e.printStackTrace();
		
		GreenContext context = GreenContext.getInstance();
		
		if(context != null && context.getResponse() != null)
		{
			JsonArray stackTrace = new JsonArray();
			JsonObject json = new JsonObject();
			json.add("stackTrace", stackTrace);
			
			json.addProperty("className", e.getClass().getName());
			json.addProperty("message", e.getMessage());
			
			for (int i = -1, s = e.getStackTrace().length; ++i < s;) {
				StackTraceElement trace = e.getStackTrace()[i];
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
	}
	
	public static void error(String msg)
	{
		System.err.println("\n["+Core.projectName+":Error]: "+msg);
		
		GreenContext context = GreenContext.getInstance();
		
		if(context != null && context.getResponse() != null)
		{
			JsonObject json = new JsonObject();			
			json.addProperty("message", msg);
			addErro(json);
		}
	}
	
	public static void clearErrors()
	{
		errors.set(null);
	}
	
	public static void warning(String msg)
	{
		System.err.println("["+Core.projectName+":Warning]: "+msg);
	}
	
	public static void log(String msg)
	{
		if(GreenCodeConfig.Console.writeLog())
			System.out.println("["+Core.projectName+"] "+msg);
	}
	
	static void addErro(JsonObject o)
	{
		JsonArray erros = errors.get();
		if(erros == null)
		{
			erros = new JsonArray();
			errors.set(erros);
		}
		
		erros.add(o);
	}
}
