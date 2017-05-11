package greencode.jscript.dom;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.elements.SelectElementPrototype;

public abstract class NodeCustom extends EventTarget {
	protected NodeCustom(Window window) {
		super(window);
	}

	public void empty() {
		DOMHandle.execCommand(this, "empty");
		if (this instanceof SelectElementPrototype) {
			((SelectElementPrototype<?>) this).options(false);
		}
	}

	public Node prependChild(Node node) {
		DOMHandle.execCommand(this, "prependChild", node);
		return node;
	}
	
	public void prependChild(String html) {
		((Node)this).insertAdjacentHTML("afterbegin", html);
	}
	
	public void appendChild(String html) {
		((Node)this).insertAdjacentHTML("beforeend", html);
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
		((Node)this).insertAdjacentHTML("beforebegin", html);
	}
	
	public void appendChildAfter(String html) {
		((Node)this).insertAdjacentHTML("afterend", html);
	}
}
