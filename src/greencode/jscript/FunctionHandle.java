package greencode.jscript;

import greencode.exception.OperationNotAllowedException;
import greencode.http.enumeration.RequestMethod;
import greencode.jscript.form.annotation.Name;
import greencode.jscript.function.implementation.Function;
import greencode.jscript.window.annotation.Form;
import greencode.kernel.GreenContext;
import greencode.kernel.LogMessage;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class FunctionHandle {
	private final transient GreenContext context = GreenContext.getInstance();
	
	private final Integer
		viewId = context == null ? null : context.getRequest().getViewSession().getId(),
		   cid = context == null ? null : context.getRequest().getConversationId();
		
	boolean isFunction = true;
	
	private transient Function dynamicEventHandler;
	private transient Method method;
	
	private String url, formName;
	
	private JsonElement methodParameters;
	
	private RequestMethod requestMethod = RequestMethod.GET;
	
	private JsonObject[] args;
	
	private boolean async = true;
	
	public FunctionHandle(String commandName, Object... parameters) {
		this.url = '#'+commandName;
		if(parameters.length > 0)
			this.methodParameters = context.gsonInstance.toJsonTree(parameters);
	}
	
	public FunctionHandle(Function o) { this.setUrl(o); }
	
	public FunctionHandle(String method) { this.setUrl(context.currentWindow().getClass(), method); }
	
	public FunctionHandle(String method, Class<? extends DOM>... args) { this.setUrl(context.currentWindow().getClass(), method, args); }
	
	public FunctionHandle(Class<? extends Window> controller, String method) { this.setUrl(controller, method); }
	
	public FunctionHandle(Class<? extends Window> controller, String method, Class<? extends DOM>... args) { this.setUrl(controller, method, args); }

	public RequestMethod getRequestMethod() { return requestMethod; }

	public void setRequestMethod(RequestMethod requestMethod) { this.requestMethod = requestMethod; }
	
	public String getUrl() { return url; }
	
	private static final HashMap<Integer, JsonObject[]> argsCached = new HashMap<Integer, JsonObject[]>();
	
	private FunctionHandle setArguments(final Class<?>... args) {		
		if(args.length > 0) {
			int hashCode = Arrays.hashCode(args);
			
			if((this.args = argsCached.get(hashCode)) != null)
				return this;
			
			this.args = new JsonObject[args.length];
			for (int i = -1; ++i < args.length;) {
				Class<?> Class = args[i];
				if(!Class.equals(GreenContext.class) && !ClassUtils.isParent(Class, DOM.class))
					throw new RuntimeException(LogMessage.getMessage("green-0035", Class.getSimpleName()));
					
				Class<?>[] classes = ClassUtils.getParents(Class);
				JsonArray fieldsName = new JsonArray();
				
				JsonObject json = new JsonObject();				
				json.addProperty("className", Class.getName());
				json.add("fields", fieldsName);
				
				if(!Class.equals(GreenContext.class)) {
					int iC = -1;
					while(!Class.equals(DOM.class)) {					
						Field[] fields = GenericReflection.getDeclaredFields(Class);
						
						for (Field f : fields) {
							if(!Modifier.isTransient(f.getModifiers()))
								fieldsName.add(context.gsonInstance.toJsonTree(f.getName()));						
						}	
						
						if(++iC == classes.length)
							break;
						
						Class = classes[i];
					}
				}
				
				this.args[i] = json;
			}
			
			argsCached.put(Arrays.hashCode(args), this.args);
		}
		
		return this;
	}
	
	public boolean isAsync() { return async; }

	/**
	 * Se usar requisição sincronizada(false), dependendo do browser, irá travar até terminar a requisição.
	 * @Default true
	 */
	public FunctionHandle setAsync(boolean async) {
		this.async = async;
		return this;
	}
	
	private static final transient HashMap<Class<?>, Method> methodsInitCached = new HashMap<Class<?>, Method>();
	
	private void setUrl(Function o) {
		if(o instanceof DOM)
			throw new OperationNotAllowedException();
		
		Class<?> _class = o.getClass();
		
		if((this.method = methodsInitCached.get(_class)) != null)
			setArguments(this.method.getParameterTypes());
		else
		{		
			Method[] methods = _class.getMethods();
			for (Method m : methods) {
				if(m.getName().equals("init")) {
					methodsInitCached.put(_class, this.method = m);
					setArguments(m.getParameterTypes());					
					break;
				}
			}
		}
		
		checkMethodAnnotation();
		
		dynamicEventHandler = o;
				
		int hashcode = o.hashCode();
		
		greencode.jscript.$Window.getRegisteredFunctions(context.currentWindow()).put(hashcode, o);
		
		this.setUrl(context.currentWindow().getClass().getSimpleName()+"$"+hashcode);
	}
	
	private void setUrl(Class<? extends Window> windowClass, String method, Class<? extends DOM>... args) {
		dynamicEventHandler = null;
		
		try {
			Class<?>[] _args;
			try {
				_args = args;
				this.method = GenericReflection.getMethod(windowClass, method, _args);
			} catch (Exception e) {
				_args = new Class<?>[args.length+1];
				_args[0] = GreenContext.class;
				for (int i = 0; ++i < _args.length;)
					_args[i] = args[i-1];
				
				this.method = GenericReflection.getMethod(windowClass, method, _args);
			}
			
			setArguments(_args);
			
			checkMethodAnnotation();
			
			this.setUrl(windowClass.getSimpleName()+"@"+method);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkMethodAnnotation() {
		if(this.method.isAnnotationPresent(Form.class)) {
			Class<? extends greencode.jscript.Form> form = this.method.getAnnotation(Form.class).value();
			
			Field[] fields = greencode.jscript.$Container.processFields(form);
			if(fields != null && fields.length > 0) {
				this.formName = form.getAnnotation(Name.class).value();
			}
		}
	}
	
	private void setUrl(String url) {
		this.url = (context != null ? context.getRequest().getContextPath(): "") +"/"+url;
	}

	public static class CONTEXT {
		public static void replaceDOM(DOM dom, FunctionHandle func) {
			if(func.dynamicEventHandler != null)
				DOMHandle.replaceReference((DOM) func.dynamicEventHandler, dom);
		}
	}
}
