package greencode.jscript;

import greencode.exception.OperationNotAllowedException;
import greencode.http.enumeration.RequestMethod;
import greencode.jscript.elements.custom.ContainerElement;
import greencode.jscript.form.annotation.Name;
import greencode.jscript.function.implementation.Function;
import greencode.jscript.window.annotation.Form;
import greencode.kernel.GreenCodeConfig;
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
		cid = context.getRequest().getConversationId(),
		viewId = context.getRequest().getViewSession().getId();
				
	private String url, formName, requestMethod = GreenCodeConfig.Server.Request.Event.methodType.toUpperCase();
	
	private JsonElement methodParameters;
		
	private JsonObject[] args;
	
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

	public RequestMethod getRequestMethod() { return RequestMethod.valueOf(requestMethod); }

	public void setRequestMethod(RequestMethod requestMethod) { this.requestMethod = requestMethod.name(); }
		
	private static final HashMap<Integer, JsonObject[]> argsCached = new HashMap<Integer, JsonObject[]>();
	
	private FunctionHandle setArguments(final Class<?>... args) {		
		if(args.length > 0) {
			int hashCode = Arrays.hashCode(args);
			
			if((this.args = argsCached.get(hashCode)) != null)
				return this;
			
			this.args = new JsonObject[args.length];
			for (int i = -1; ++i < args.length;) {
				Class<?> clazz = args[i];
				if(!clazz.equals(GreenContext.class) && !ClassUtils.isParent(clazz, DOM.class))
					throw new RuntimeException(LogMessage.getMessage("green-0035", clazz.getSimpleName()));
								
				JsonObject json = new JsonObject();				
				
				if(!clazz.equals(GreenContext.class)) {
					if(ClassUtils.isParent(clazz, ContainerElement.class))
						clazz = ContainerElement.class;
					else if(ClassUtils.isParent(clazz, Element.class)) {
						json.addProperty("castTo", clazz.getName());
						clazz = Element.class;
					} else {
						JsonArray fieldsName = new JsonArray();
						json.add("fields", fieldsName);
						
						final Class<?>[] classes = ClassUtils.getParents(clazz, DOM.class);
						Class<?> parent = clazz;
						for (int j = -1; ++j < classes.length;) {
							Field[] fields = GenericReflection.getDeclaredFields(parent);							
							for (Field f : fields) {
								if(!Modifier.isTransient(f.getModifiers()))
									fieldsName.add(context.gsonInstance.toJsonTree(f.getName()));						
							}
							
							parent = classes[i];
						}
					}
				}
				
				json.addProperty("className", clazz.getName());				
				this.args[i] = json;
			}
			
			argsCached.put(Arrays.hashCode(args), this.args);
		}
		
		return this;
	}
	
	private static final transient HashMap<Class<?>, Method> methodsInitCached = new HashMap<Class<?>, Method>();
	
	private void setUrl(Function o) {
		if(o instanceof DOM)
			throw new OperationNotAllowedException();
		
		Class<?> _class = o.getClass();
		
		Method method;
		
		if((method = methodsInitCached.get(_class)) != null)
			setArguments(method.getParameterTypes());
		else
		{		
			Method[] methods = _class.getMethods();
			for (Method m : methods) {
				if(m.getName().equals("init")) {
					methodsInitCached.put(_class, method = m);
					setArguments(m.getParameterTypes());					
					break;
				}
			}
		}
		
		checkMethodAnnotation(method);
				
		int hashcode = o.hashCode();
		
		greencode.jscript.$Window.getRegisteredFunctions(context.currentWindow()).put(hashcode, o);
		
		this.setUrl(context.currentWindow().getClass().getSimpleName()+"$"+hashcode);
	}
	
	private void setUrl(Class<? extends Window> windowClass, String methodName, Class<? extends DOM>... args) {
		try {
			Method method;
			Class<?>[] _args;
			try {
				_args = args;
				method = GenericReflection.getMethod(windowClass, methodName, _args);
			} catch (Exception e) {
				_args = new Class<?>[args.length+1];
				_args[0] = GreenContext.class;
				for (int i = 0; ++i < _args.length;)
					_args[i] = args[i-1];
				
				method = GenericReflection.getMethod(windowClass, methodName, _args);
			}
			
			setArguments(_args);
			
			checkMethodAnnotation(method);
			
			this.setUrl(windowClass.getSimpleName()+"@"+methodName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkMethodAnnotation(Method method) {
		if(method.isAnnotationPresent(Form.class)) {
			Class<? extends greencode.jscript.Form> form = method.getAnnotation(Form.class).value();
			
			Field[] fields = greencode.jscript.$Container.processFields(form);
			if(fields != null && fields.length > 0) {
				this.formName = form.getAnnotation(Name.class).value();
			}
		}
	}
	
	private void setUrl(String url) {
		this.url = (context != null ? greencode.kernel.$GreenContext.getContextPath() : "") +"/"+url;
	}
	
	public static void destroy(Window window, Function anonymousClass) {
		window.functions.remove(anonymousClass.hashCode());
	}
}
