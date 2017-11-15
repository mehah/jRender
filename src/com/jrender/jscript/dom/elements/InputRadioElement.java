package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputRadioElement<T> extends InputElementCheckable<T> {
	protected InputRadioElement(Window window) {
		super("radio", window);
	}

	private InputRadioElement(Window window, Class<?> typeValue) {
		super("radio", window, typeValue);
	}

	public static InputRadioElement<String> cast(Element e) {
		return ElementHandle.cast(e, InputRadioElement.class);
	}
	
	public static<T> InputRadioElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, InputRadioElement.class, type);
	}
}
