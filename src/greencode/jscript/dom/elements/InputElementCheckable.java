package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Window;

public abstract class InputElementCheckable<T> extends InputElementDisabling<T> {
		
	protected InputElementCheckable(String type, Window window) { super(type, window); }
	
	protected InputElementCheckable(String type, Window window, Class<?> typeValue) { super(type, window, typeValue); }
	
	public void checked(Boolean checked) { DOMHandle.setProperty(this, "checked", checked); }
	
	public Boolean checked() { return DOMHandle.getVariableValueByProperty(this, "checked", Boolean.class, "checked"); }
	
	public void defaultChecked(Boolean defaultChecked) { DOMHandle.setProperty(this, "defaultChecked", defaultChecked); }
	
	public Boolean defaultChecked() { return DOMHandle.getVariableValueByProperty(this, "defaultChecked", Boolean.class, "defaultChecked"); }
}
