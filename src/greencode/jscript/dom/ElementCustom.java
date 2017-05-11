package greencode.jscript.dom;

import greencode.jscript.DOMHandle;

public abstract class ElementCustom extends Node {
	protected ElementCustom(Window window) {
		super(window);
	}

	public Element getElementOrCreateByTagName(String tagName) {
		Element e = new Element(greencode.jscript.$DOMHandle.getWindow(this));
		DOMHandle.registerReturnByCommand(this, e, "getElementOrCreateByTagName", tagName);
		return e;
	}

	public void addClass(String className) {
		DOMHandle.execCommand(this, "addClass", className);
	}

	public void removeClass(String className) {
		DOMHandle.execCommand(this, "removeClass", className);
	}
}
