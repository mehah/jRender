package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputTextElement<T> extends InputElementTextField<T> {
	protected InputTextElement(Window window) {
		super("text", window);
	}

	private InputTextElement(Window window, Class<?> typeValue) {
		super("text", window, typeValue);
	}

	public static InputTextElement<String> cast(Element e) {
		return ElementHandle.cast(e, InputTextElement.class);
	}
	
	public static<T> InputTextElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, InputTextElement.class, type);
	}
}
