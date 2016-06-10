package greencode.jscript.dom;

import greencode.http.ViewSession;
import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;

public abstract class EventTarget extends DOM {
	protected EventTarget(Window window) {
		super(window);
	}

	EventTarget(ViewSession viewSession) {
		super(viewSession);
	}

	public void addEventListener(String eventName, FunctionHandle handle) {
		DOMHandle.execCommand(this, "registerEvent", eventName, handle);
	}

	public void addEventListener(String eventName, FunctionHandle handle, Object... args) {
		DOMHandle.execCommand(this, "registerEvent", eventName, handle, args);
	}

	public void dispatchEvent(String eventName) {
		DOMHandle.execCommand(this, "shootEvent", eventName);
	}
	
	public void removeEventListener(String eventName, FunctionHandle handle) {
		DOMHandle.execCommand(this, "removeEvent", eventName, handle);
	}

	public void removeEventListener(String eventName) {
		DOMHandle.execCommand(this, "removeEvent", eventName);
	}
}
