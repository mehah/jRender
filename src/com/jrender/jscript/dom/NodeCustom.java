package com.jrender.jscript.dom;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.elements.SelectElementPrototype;

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

	public<N extends Node> N prependChild(N node) {
		DOMHandle.execCommand(this, "prependChild", node);
		return node;
	}

	public<N extends Node> N appendChildBefore(N node) {
		DOMHandle.execCommand(this, "appendChildBefore", node);
		return node;
	}

	public<N extends Node> N appendChildAfter(N node) {
		DOMHandle.execCommand(this, "appendChildAfter", node);
		return node;
	}
}
