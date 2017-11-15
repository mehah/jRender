package com.jrender.jscript.dom;

import com.jrender.jscript.DOMHandle;

public abstract class ElementCustom extends Node {
	protected ElementCustom(Window window) {
		super(window);
	}

	public Element getElementOrCreateByTagName(String tagName) {
		Element e = new Element(com.jrender.jscript.$DOMHandle.getWindow(this));
		DOMHandle.registerReturnByCommand(this, e, "getElementOrCreateByTagName", tagName);
		return e;
	}

	public void addClass(String className) {
		DOMHandle.execCommand(this, "addClass", className);
	}

	public void removeClass(String className) {
		DOMHandle.execCommand(this, "removeClass", className);
	}
	
	public void prependChild(String html, Object... args) {
		((Element)this).insertAdjacentHTML("afterbegin", html, args);
	}

	public void appendChild(String html, Object... args) {
		((Element)this).insertAdjacentHTML("beforeend", html, args);
	}

	public void appendChildBefore(String html, Object... args) {
		((Element)this).insertAdjacentHTML("beforebegin", html, args);
	}

	public void appendChildAfter(String html, Object... args) {
		((Element)this).insertAdjacentHTML("afterend", html, args);
	}
}
