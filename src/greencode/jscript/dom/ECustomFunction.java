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

	public Node prepend(Node node) {
		DOMHandle.execCommand(this, "prepend", node);
		return node;
	}

	public Node appendBefore(Node node) {
		DOMHandle.execCommand(this, "appendBefore", node);
		return node;
	}

	public Node appendAfter(Node node) {
		DOMHandle.execCommand(this, "appendAfter", node);
		return node;
	}
}
