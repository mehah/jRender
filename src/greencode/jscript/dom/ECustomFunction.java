package greencode.jscript.dom;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.elements.SelectElementPrototype;

public abstract class ECustomFunction extends Node {
	protected ECustomFunction(Window window) {
		super(window);
	}

	public void empty() {
		DOMHandle.execCommand(this, "empty");
		if (this instanceof SelectElementPrototype) {
			((SelectElementPrototype<?>) this).options(false);
		}
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

	public Node prependChild(Node node) {
		DOMHandle.execCommand(this, "prependChild", node);
		return node;
	}
	
	public void prependChild(String html) {
		insertAdjacentHTML("afterbegin", html);
	}
	
	public void appendChild(String html) {
		insertAdjacentHTML("beforeend", html);
	}

	public Node appendChildBefore(Node node) {
		DOMHandle.execCommand(this, "appendChildBefore", node);
		return node;
	}

	public Node appendChildAfter(Node node) {
		DOMHandle.execCommand(this, "appendChildAfter", node);
		return node;
	}
	
	public void appendChildBefore(String html) {
		insertAdjacentHTML("beforebegin", html);
	}
	
	public void appendChildAfter(String html) {
		insertAdjacentHTML("afterend", html);
	}
}
