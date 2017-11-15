package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputPasswordElement extends InputElementTextField<String> {
	protected InputPasswordElement(Window window) {
		super("password", window);
	}
	
	private InputPasswordElement(Window window, Class<?> typeValue) {
		super("password", window, typeValue);
	}

	public static InputPasswordElement cast(Element e) {
		return ElementHandle.cast(e, InputPasswordElement.class);
	}
}
