package com.jrender.jscript.dom.elements;

import java.lang.reflect.ParameterizedType;

import com.jrender.exception.JRenderError;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.WindowHandle;
import com.jrender.util.ClassUtils;
import com.jrender.util.LogMessage;

public abstract class InputElement<T> extends Element {
	private final Class<T> typeValue;

	protected InputElement(String type, Window window) {
		this(type, window, null);
	}

	protected InputElement(String type, Window window, Class<?> typeValue) {
		super(window, "input");
		DOMHandle.setVariableValue(this, "type", type);

		Class<?> classUnnamed = this.getClass();
		if (classUnnamed.getGenericSuperclass() instanceof Class)
			throw new JRenderError(LogMessage.getMessage("0046", ((Class<?>) classUnnamed.getGenericSuperclass()).getSimpleName()));

		this.typeValue = (Class<T>) (typeValue == null ? ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] : typeValue);
	}

	public Form form() {
		return null;
	}

	public void name(String name) {
		DOMHandle.setProperty(this, "name", name);
	}

	public String name() {
		return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name");
	}

	public void type(String type) {
		DOMHandle.setProperty(this, "type", type);
	}

	public String type() {
		return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type");
	}

	public void value(T value) {
		if(value != null && !ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
			WindowHandle.registerObjectParamter(window, value);
		}
		
		DOMHandle.setProperty(this, "value", value == null ? "" : value.toString());
	}

	public T value() {
		return DOMHandle.getVariableValueByProperty(this, "value", typeValue, "value");
	}

	public void disabled(boolean value) {
		DOMHandle.setProperty(this, "disabled", value);
	}

	public Boolean disabled() {
		return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled");
	}
}
