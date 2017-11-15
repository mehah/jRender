package com.jrender.jscript.dom;

import com.jrender.http.ViewSession;
import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;

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
