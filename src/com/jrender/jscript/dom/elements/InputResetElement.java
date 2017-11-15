package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputResetElement extends InputElementDisabling<String> {
	protected InputResetElement(Window window) {
		super("reset", window);
	}

	public static InputResetElement cast(Element e) {
		return ElementHandle.cast(e, InputResetElement.class);
	}
}
