package greencode.jscript;

import greencode.http.Conversation;
import greencode.jscript.function.implementation.Function;

import java.util.HashMap;

public class $Window {
	private $Window() {}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Class<? extends Window>, Window> getMap(Conversation conversation) {
		HashMap<Class<? extends Window>, Window> listHttpActions = (HashMap<Class<? extends Window>, Window>) conversation.getAttribute("LIST_WINDOW_CONTROLLER");
		if(listHttpActions == null)
			conversation.setAttribute("LIST_WINDOW_CONTROLLER", listHttpActions = new HashMap<Class<? extends Window>, Window>());
		
		return listHttpActions;
	}
	
	public static HashMap<Integer, Function> getRegisteredFunctions(Window window) {
		return window.functions == null ? window.functions = new HashMap<Integer, Function>() : window.functions;
	}
}
