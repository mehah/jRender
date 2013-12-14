package greencode.jscript;

import greencode.http.HttpAction;
import greencode.http.ViewSession;
import greencode.jscript.function.implementation.Function;
import greencode.jscript.function.implementation.SimpleFunction;
import greencode.kernel.Console;
import greencode.kernel.GreenContext;

import java.util.HashMap;

public abstract class Window extends DOM implements HttpAction {	
	private HashMap<Integer, Function> functions;
	
	public final Window window = this;
	public final Document document;
	public final Location location;
	public final History history;
	public final Screen screen;
	public final Navigator navigator;
	
	protected Window() {
		this(GreenContext.getInstance());
	}
	
	private Window(GreenContext context) {
		super(context.getRequest().getViewSession());
		
		uid = 2; // WINDOW ID
		
		location = new Location(context.getRequest(), this);
		history = new History(this);
		screen = new Screen(this);
		navigator = new Navigator(context.getRequest(), this);
		
		Document doc = (Document) viewSession.getAttribute("WINDOW_DOCUMENT");
		if(doc == null)
		{
			doc = new Document(viewSession);
			viewSession.setAttribute("WINDOW_DOCUMENT", doc);
		}
		
		this.document = doc;
	}
		
	public abstract void onLoad();
	
	public int setTimeout(SimpleFunction func, int time)
	{
		return setTimeout(new FunctionHandle(func), time);
	}
	
	public int setInterval(SimpleFunction func, int time)
	{
		return setInterval(new FunctionHandle(func), time);
	}
	
	public int setTimeout(FunctionHandle handle, int time)
	{
		int hashcode = handle.hashCode();
		DOMHandle.registerReturnByCommand(this, hashcode, "setTimeout", handle, time);
		return hashcode;
	}
	
	public int setInterval(FunctionHandle handle, int time)
	{
		int hashcode = handle.hashCode();
		DOMHandle.registerReturnByCommand(this, hashcode, "setInterval", handle, time);
		return hashcode;
	}
	
	public void addEventListener(String eventName, FunctionHandle handle)
	{
		DOMHandle.execCommand(this, "crossbrowser.registerEvent", eventName, handle);
	}
	
	public void dispatchEvent(String eventName)
	{
		DOMHandle.execCommand(this, "crossbrowser.shootEvent", eventName);
	}
	
	public void removeEventListener(String eventName, FunctionHandle handle)
	{
		DOMHandle.execCommand(this, "crossbrowser.removeEvent", eventName, handle);
	}

	public void clearTimeout(int uid)
	{
		DOMHandle.execCommand(this, "clearTimeout", DOMHandle.getRegisteredReturn(uid));
		DOMHandle.removeRegisteredReturn(this, uid);
	}
	
	public void clearInterval(int uid)
	{
		DOMHandle.execCommand(this, "clearInterval", DOMHandle.getRegisteredReturn(uid));
		DOMHandle.removeRegisteredReturn(this, uid);
	}
	
	public void eval(String expression)
	{
		DOMHandle.execCommand(this, "eval", expression);
	}
	
	public void alert(String text)
	{
		DOMHandle.execCommand(this, "alert", text);
	}
	
	public final static class Context {
		private Context() {}
		
		@SuppressWarnings("unchecked")
		public static HashMap<Class<? extends Window>, Window> getMap(ViewSession viewSession)
		{
			HashMap<Class<? extends Window>, Window> listHttpActions = (HashMap<Class<? extends Window>, Window>) viewSession.getAttribute("LIST_WINDOW_CONTROLLER");
			if(listHttpActions == null)
			{
				listHttpActions = new HashMap<Class<? extends Window>, Window>();
				viewSession.setAttribute("LIST_WINDOW_CONTROLLER", listHttpActions);
			}
			return listHttpActions;
		}
		
		@SuppressWarnings("unchecked")
		public static <A extends Window> A getInstance(Class<A> actionClass, GreenContext context)
		{			
			HashMap<Class<? extends Window>, Window> list = getMap(context.getRequest().getViewSession());
			
			Window action = list.get(actionClass);
			
			if(action == null)
			{
				try {
					action = actionClass.newInstance();
					
					list.put((Class<? extends Window>) actionClass, action);
				} catch (Exception e) {
					Console.error(e);
				}
			}
			
			return (A) action;
		}
		
		/*public static void removeInstance(Window action)
		{
			//action.disappear();
		}
		
		public static void removeInstance(Class<? extends Window> classAction, Conversation conversation)
		{
			HttpAction action = getMap(conversation).get(classAction);
			if(action != null)
				action.disappear();
		}*/
		
		public static HashMap<Integer, Function> getRegisteredFunctions(Window window)
		{
			if(window.functions == null)
				window.functions = new HashMap<Integer, Function>();
			
			return window.functions;
		}
	}
}
