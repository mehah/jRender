package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Window;

public abstract class InputElementTextField<T> extends InputElementDisabling<T> {
		
	protected InputElementTextField(String type, Window window) { super(type, window); }
	
	protected InputElementTextField(String type, Window window, Class<?> typeValue) { super(type, window, typeValue); }
	
	public void maxLength(Integer maxLength) { DOMHandle.setProperty(this, "maxLength", maxLength); }
	
	public Integer maxLength() { return DOMHandle.getVariableValueByProperty(this, "maxLength", Integer.class, "maxLength"); }
	
	public void size(Integer size) { DOMHandle.setProperty(this, "maxLength", size); }
	
	public Integer size() { return DOMHandle.getVariableValueByProperty(this, "size", Integer.class, "size"); }
	
	public void defaultValue(T defaultValue) { DOMHandle.setProperty(this, "defaultValue", defaultValue); }
	
	public T defaultValue() { return (T) DOMHandle.getVariableValueByProperty(this, "defaultValue", Object.class, "defaultValue"); }
	
	public void readOnly(Boolean readOnly) { DOMHandle.setProperty(this, "readOnly", readOnly); }
	
	public Boolean readOnly() { return DOMHandle.getVariableValueByProperty(this, "readOnly", Boolean.class, "readOnly"); }
	
	public void select() { DOMHandle.execCommand(this, "select"); }
}
