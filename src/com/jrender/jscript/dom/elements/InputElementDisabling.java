package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Window;

public abstract class InputElementDisabling<T> extends InputElement<T> {

	protected InputElementDisabling(String type, Window window) {
		super(type, window);
	}

	protected InputElementDisabling(String type, Window window, Class<?> typeValue) {
		super(type, window, typeValue);
	}

	public void disabled(Boolean disabled) {
		DOMHandle.setProperty(this, "disabled", disabled);
	}

	public Boolean disabled() {
		return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled");
	}
}
