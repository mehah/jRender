package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Window;

public abstract class InputElementTextField extends InputElementDisabling {
		
	protected InputElementTextField(String type, Window window) { super(type, window); }
	
	public void maxLength(Integer maxLength) { DOMHandle.setProperty(this, "maxLength", maxLength); }
	
	public Integer maxLength() { return DOMHandle.getVariableValueByProperty(this, "maxLength", Integer.class, "maxLength"); }
	
	public void size(Integer size) { DOMHandle.setProperty(this, "maxLength", size); }
	
	public Integer size() { return DOMHandle.getVariableValueByProperty(this, "size", Integer.class, "size"); }
	
	public void defaultValue(String defaultValue) { DOMHandle.setProperty(this, "defaultValue", defaultValue); }
	
	public String defaultValue() { return DOMHandle.getVariableValueByProperty(this, "defaultValue", String.class, "defaultValue"); }
	
	public void readOnly(Boolean readOnly) { DOMHandle.setProperty(this, "readOnly", readOnly); }
	
	public Boolean readOnly() { return DOMHandle.getVariableValueByProperty(this, "readOnly", Boolean.class, "readOnly"); }
	
	public void select() { DOMHandle.execCommand(this, "select"); }
}
