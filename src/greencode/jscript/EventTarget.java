package greencode.jscript;

import greencode.http.ViewSession;

public abstract class EventTarget extends DOM {
	protected EventTarget(Window window) { super(window); }	
	EventTarget(ViewSession viewSession) { super(viewSession); }
	
	/**
	 * No internet explorer irá usar attachEvent
	 */
	public void addEventListener(String eventName, FunctionHandle handle)
	{ DOMHandle.execCommand(this, "@crossbrowser.registerEvent", eventName, handle); }
	
	public void addEventListener(String eventName, FunctionHandle handle, Object... args)
	{ DOMHandle.execCommand(this, "@crossbrowser.registerEvent", eventName, handle, args); }
	
	/**
	 * No internet explorer irá usar fireEvent
	 */
	public void dispatchEvent(String eventName)
	{ DOMHandle.execCommand(this, "@crossbrowser.shootEvent", eventName); }
	
	/**
	 * No internet explorer irá usar detachEvent
	 */
	public void removeEventListener(String eventName, FunctionHandle handle)
	{ DOMHandle.execCommand(this, "@crossbrowser.removeEvent", eventName, handle); }
	
	public void removeEventListener(String eventName)
	{ DOMHandle.execCommand(this, "@crossbrowser.removeEvent", eventName); }
}
