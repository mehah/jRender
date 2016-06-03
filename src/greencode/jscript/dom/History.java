package greencode.jscript.dom;

import java.util.HashMap;

import greencode.jscript.DOMHandle;

public final class History {
	private final Window window;
	
	History(Window window) { this.window = window; }
	
	public int length() { return DOMHandle.getVariableValueByProperty(window, "history.length", int.class, "history.length"); }
	public void back() { DOMHandle.execCommand(window, "history.back"); }
	public void forward() { DOMHandle.execCommand(window, "history.forward"); }
	public void go(int index) { DOMHandle.execCommand(window, "history.go", index); }
	public void go(String url) { DOMHandle.execCommand(window, "history.go", url); }
	
	public void pushState(HashMap<String, Object> stateObject, String title) { DOMHandle.execCommand(window, "history.pushState", stateObject, title); }
	public void replaceState(HashMap<String, Object> stateObject, String title) { DOMHandle.execCommand(window, "history.replaceState", stateObject, title); }
	public void pushState(HashMap<String, Object> stateObject, String title, String URL) { DOMHandle.execCommand(window, "history.pushState", stateObject, title, URL); }
	public void replaceState(HashMap<String, Object> stateObject, String title, String URL) { DOMHandle.execCommand(window, "history.replaceState", stateObject, title, URL); }
}
