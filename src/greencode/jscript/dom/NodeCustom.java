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

	public Node appendChildBefore(Node node) {
		DOMHandle.execCommand(this, "appendChildBefore", node);
		return node;
	}

	public Node appendChildAfter(Node node) {
		DOMHandle.execCommand(this, "appendChildAfter", node);
		return node;
	}
}
