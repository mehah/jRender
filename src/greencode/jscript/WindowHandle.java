package greencode.jscript;

import greencode.exception.GreencodeError;
import greencode.http.Conversation;
import greencode.jscript.window.annotation.RegisterPage;
import greencode.jscript.window.listener.WindowDestroyListener;
import greencode.kernel.LogMessage;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public final class WindowHandle {
	
	public static Window getInstance(Element e) {
		return e.window;
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Window> A getInstance(Class<A> actionClass, Conversation conversation) {			
		Map<Class<? extends Window>, Window> list = $Window.getMap(conversation);
		
		Window action = list.get(actionClass);
		
		if(action == null) {
			try {
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
				throw new GreencodeError(e);
			}
		}
		
		return (A) action;
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Window> A removeInstance(Class<A> actionClass, Conversation conversation) {			
		Map<Class<? extends Window>, Window> list = $Window.getMap(conversation);		
		
		A w = (A) list.remove(actionClass);
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
		
		return w;
	}
	
	public static greencode.jscript.window.annotation.Page getPage(Class<? extends Window> controllerClass) {
		return getPageByName(controllerClass, null);
	}
	
	public static greencode.jscript.window.annotation.Page getPageByName(Class<? extends Window> controllerClass, String pageName) {
		greencode.jscript.window.annotation.Page page = controllerClass.getAnnotation(greencode.jscript.window.annotation.Page.class);
		if(page == null) {
			RegisterPage rp = controllerClass.getAnnotation(RegisterPage.class);
			if(pageName == null || pageName.isEmpty())
				page = rp.value()[0];
			else {
				for (greencode.jscript.window.annotation.Page p : rp.value()) {
					if(p.name().equals(pageName)) {
						page = p;
						break;
					}						
				}
				
				if(page == null)
					throw new RuntimeException(LogMessage.getMessage("green-0030", pageName, controllerClass.getSimpleName()));
			}
		}
		
		return page;
	}
}
