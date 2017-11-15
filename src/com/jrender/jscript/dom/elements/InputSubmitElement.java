package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputSubmitElement extends InputElementDisabling<String> {
	protected InputSubmitElement(Window window) {
		super("submit", window);
	}

	public static InputSubmitElement cast(Element e) {
		return ElementHandle.cast(e, InputSubmitElement.class);
	}
}
