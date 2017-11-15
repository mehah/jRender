package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class SelectElement<T> extends SelectElementPrototype<T> {
	protected SelectElement(Window window) {
		this(window, null);
	}
	
	protected SelectElement(Window window, Class<?> typeValue) {
		super("select-one", window, typeValue);
	}

	public static<T> SelectElement<T> cast(Element e) {
		return ElementHandle.cast(e, SelectElement.class);
	}
	
	public static<T> SelectElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, SelectElement.class, type);
	}

	/**
	 * CUSTOM METHOD
	 * 
	 * @return String
	 */
	public T selectedValue() {
		return (T) DOMHandle.getVariableValue(this, "value", typeValue);
	}

	/**
	 * CUSTOM METHOD
	 */
	public void selectedValue(T value) {
		DOMHandle.setVariableValue(this, "value", value);
		DOMHandle.execCommand(this, "selectOptionByValue", value);
	}
}
