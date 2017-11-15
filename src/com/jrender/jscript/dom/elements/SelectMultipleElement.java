package com.jrender.jscript.dom.elements;

import com.jrender.exception.JRenderError;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Window;
import com.jrender.util.LogMessage;

public class SelectMultipleElement<T> extends SelectElementPrototype<T> {

	protected SelectMultipleElement(Window window) {
		this(window, null);
	}
	
	protected SelectMultipleElement(Window window, Class<?> typeValue) {
		super("select-multiple", window, typeValue);
		
		if(this.typeValue.isArray())
			throw new JRenderError(LogMessage.getMessage("0050", getClass().getSimpleName()));
	}

	// CUSTOM METHOD
	public T[] selectedValues() {
		return (T[]) DOMHandle.getVariableValue(this, "value", this.typeValue);
	}
}
