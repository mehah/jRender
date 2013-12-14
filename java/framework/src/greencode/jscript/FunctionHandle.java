package greencode.jscript;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;

import greencode.exception.OperationNotAllowedException;
import greencode.http.enumeration.RequestMethod;
import greencode.jscript.function.implementation.Function;
import greencode.kernel.GreenContext;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

public final class FunctionHandle {
	private final transient GreenContext context = GreenContext.getInstance();
		
	boolean isFunction = true;
	
	private String url;
	
	private RequestMethod requestMethod = RequestMethod.GET;
	
	private JsonObject[] args;
	
	private boolean async = true;
	
	private JsonObject parametersRequest;
	
	transient Function dynamicEventHandler;
	
	public FunctionHandle(String commandName, Object... parameters)
	{
		this.url = '#'+commandName;
		if(parameters.length > 0)
		{
			this.parametersRequest = new JsonObject();
			this.parametersRequest.add("methodParameters", context.getGsonInstance().toJsonTree(parameters));
		}	
	}
	
	//TODO: Criar cache para os parametros
	public FunctionHandle(Function o) {
		this.setUrl(o);
	}
	
	public FunctionHandle(Class<? extends Window> controller) {
		this.setUrl(controller, "init");
	}
	
	public FunctionHandle(Class<? extends Window> controller, String method) {
		this.setUrl(controller, method);
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}
	
	public String getUrl() {
		return url;
	}
	
	private static HashMap<Integer, JsonObject[]> argsCached = new HashMap<Integer, JsonObject[]>();
	
	public FunctionHandle setArguments(Class<?>... args)
	{
		if(this.dynamicEventHandler != null)
			throw new OperationNotAllowedException();
		
		if(args.length > 0)
		{
			int hashCode = Arrays.hashCode(args);
			
			this.args = argsCached.get(hashCode);
			if(this.args != null)
				return this;
			
			this.args = new JsonObject[args.length];
			for (int i = -1; ++i < args.length;) {
				Class<?> Class = args[i];
				Class<?>[] classes = ClassUtils.getParents(Class);
				List<String> fieldsName = new ArrayList<String>();
				
				int iC = -1;
				while(true)
				{	
					Field[] fields = GenericReflection.getDeclaredFields(Class);
					
					for (int j = -1; ++j < fields.length;) {
						Field f = fields[j];
						fieldsName.add(f.getName());
					}
					
					if(++iC == classes.length)
						break;
					
					Class = classes[i];
				}
				
				JsonObject json = new JsonObject();
				
				json.addProperty("className", Class.getName());
				json.add("fields", context.getGsonInstance().toJsonTree(fieldsName));
				
				this.args[i] = json;
			}
			
			argsCached.put(Arrays.hashCode(args), this.args);
		}
		
		return this;
	}
	
	public boolean isAsync() {
		return async;
	}

	/**
	 * Se usar requisição sincronizada(false), dependendo do browser, irá travar até terminar a requisição.
	 * @Default true
	 */
	public FunctionHandle setAsync(boolean async) {
		this.async = async;
		return this;
	}
	
	private void setUrl(Function o) {
		if(o instanceof DOM)
			throw new OperationNotAllowedException();
		
		Class<?> _class = o.getClass();
		
		Method[] methods = _class.getMethods();
		for (int i = -1, s = methods.length; ++i < s;)
		{
			Method m = methods[i];
			
			if(m.getName().equals("init"))
			{
				setArguments(m.getParameterTypes());
				break;
			}
		}
		
		dynamicEventHandler = o;
				
		int hashcode = o.hashCode();
		
		Window.Context.getRegisteredFunctions(context.getCurrentWindow()).put(hashcode, o);
		
		this.setUrl(context.getCurrentWindow().getClass().getSimpleName()+"$"+hashcode);
	}
	
	private void setUrl(Class<? extends Window> httpAction, String method) {
		dynamicEventHandler = null;
		
		this.setUrl(httpAction.getSimpleName()+"@"+method);
	}
	
	private void setUrl(String url) {		
		this.url = context.getRequest().getContextPath()+"/"+url;
	}

	public static class CONTEXT {
		public static void replaceDOM(DOM dom, FunctionHandle func)
		{
			if(func.dynamicEventHandler != null)
			{
				DOMHandle.replaceReference((DOM) func.dynamicEventHandler, dom);
			}	
		}
	}
}
