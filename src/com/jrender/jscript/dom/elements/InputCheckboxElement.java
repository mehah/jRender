package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputCheckboxElement<T> extends InputElementCheckable<T> {
	protected InputCheckboxElement(Window window) {
		super("checkbox", window);
	}

	private InputCheckboxElement(Window window, Class<?> typeValue) {
		super("checkbox", window, typeValue);
	}

	public static<T> InputCheckboxElement<T> cast(Element e) {
		return ElementHandle.cast(e, InputCheckboxElement.class);
	}
	
	public static<T> InputCheckboxElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, InputCheckboxElement.class, type);
	}
}
