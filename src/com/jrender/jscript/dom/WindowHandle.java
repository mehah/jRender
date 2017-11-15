package com.jrender.jscript.dom;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.jrender.exception.JRenderError;
import com.jrender.http.Conversation;
import com.jrender.jscript.dom.window.annotation.RegisterPage;
import com.jrender.jscript.dom.window.listener.WindowDestroyListener;
import com.jrender.kernel.Console;
import com.jrender.util.ClassUtils;
import com.jrender.util.GenericReflection;
import com.jrender.util.LogMessage;

public final class WindowHandle {
		
	@SuppressWarnings("unchecked")
	public static <A extends Window> A getInstance(Class<A> actionClass, Conversation conversation) {			
		Map<Class<? extends Window>, Window> list = $Window.getMap(conversation);
		
		Window action = list.get(actionClass);
		
		if(action == null) {
			try {
				Console.log("Creating Controller: "+actionClass.getSimpleName());
				action = actionClass.newInstance();
				list.put((Class<? extends Window>) actionClass, action);
				
				Window lastWindow = null;
				Class<?>[] parents = ClassUtils.getParents(actionClass, Window.class);
				for (Class<?> p : parents) {
					final Class<? extends Window> c = (Class<? extends Window>) p;
					
					if(!Modifier.isAbstract(c.getModifiers()))
						lastWindow = getInstance(c, conversation);
					else if(lastWindow == null)
						break;
					
					for (Field f : GenericReflection.getDeclaredFields(c))
						if(!Modifier.isStatic(f.getModifiers()))
							f.set(action, f.get(lastWindow));
				}
			} catch (Exception e) {
				throw new JRenderError(e);
			}
		}
		
		return (A) action;
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Window> A removeInstance(Class<A> actionClass, Conversation conversation) {			
		Map<Class<? extends Window>, Window> list = $Window.getMap(conversation);		
		
		A w = (A) list.remove(actionClass);
		
		if(w != null) {
			if(w.functions != null) {
				w.functions.clear();
				w.functions = null;
			}
			
			if(w.objectParameters != null){
				w.objectParameters.clear();
				w.objectParameters = null;
			}
			
			if(w instanceof WindowDestroyListener)
				((WindowDestroyListener) w).onDestroy();
		}
		
		return w;
	}
	
	public static com.jrender.jscript.dom.window.annotation.Page getPage(Class<? extends Window> controllerClass) {
		return getPageByName(controllerClass, null);
	}
	
	public static com.jrender.jscript.dom.window.annotation.Page getPageByName(Class<? extends Window> controllerClass, String pageName) {
		com.jrender.jscript.dom.window.annotation.Page page = controllerClass.getAnnotation(com.jrender.jscript.dom.window.annotation.Page.class);
		if(page == null) {
			RegisterPage rp = controllerClass.getAnnotation(RegisterPage.class);
			if(pageName == null || pageName.isEmpty())
				page = rp.value()[0];
			else {
				for (com.jrender.jscript.dom.window.annotation.Page p : rp.value()) {
					if(p.name().equals(pageName)) {
						page = p;
						break;
					}						
				}
				
				if(page == null)
					throw new RuntimeException(LogMessage.getMessage("0030", pageName, controllerClass.getSimpleName()));
			}
		}
		
		return page;
	}
	
	private static Map<String, Object> getObjectParameters(Window window) {
		return window.objectParameters == null ? window.objectParameters = new HashMap<String, Object>() : window.objectParameters;
	}
	
	public static Object getObjectParamter(Window window, String key) {
		return getObjectParameters(window).get(key);
	}

	public static void registerObjectParamter(Window window, Object value) {
		getObjectParameters(window).put(value.toString(), value);
	}

	public static void removeObjectParamter(Window window, Object value) {
		getObjectParameters(window).remove(value.toString());
	}

	public static void clearObjectParamter(Window window) {
		getObjectParameters(window).clear();
	}
}
