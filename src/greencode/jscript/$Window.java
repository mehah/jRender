package greencode.jscript;

import greencode.http.Conversation;
import greencode.jscript.function.implementation.Function;
import greencode.jscript.window.annotation.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class $Window {
	private $Window() {}
	
	@SuppressWarnings("unchecked")
	public static Map<Class<? extends Window>, Window> getMap(Conversation conversation) {
		ConcurrentHashMap<Class<? extends Window>, Window> listHttpActions = (ConcurrentHashMap<Class<? extends Window>, Window>) conversation.getAttribute("LIST_WINDOW_CONTROLLER");
		if(listHttpActions == null)
			conversation.setAttribute("LIST_WINDOW_CONTROLLER", listHttpActions = new ConcurrentHashMap<Class<? extends Window>, Window>());
		
		return listHttpActions;
	}
	
	public static HashMap<Integer, Function> getRegisteredFunctions(Window window) {
		return window.functions == null ? window.functions = new HashMap<Integer, Function>() : window.functions;
	}
	
	public static Page getCurrentPageAnnotation(Window window) {
		return window.currentPageAnnotation;
	}
	
	public static void setCurrentPageAnnotation(Window window, Page page) {
		window.currentPageAnnotation = page;
	}
}
