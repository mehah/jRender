package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.Form;
import greencode.jscript.Window;

public abstract class InputElement<T> extends Element {
		
	protected InputElement(String type, Window window) {
		super(window, "input");
		DOMHandle.setVariableValue(this, "type", type);
	}
	
	public Form form() { return null;}
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public void type(String type) { DOMHandle.setProperty(this, "type", type); }
	
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public void value(T value) { DOMHandle.setProperty(this, "value", value); }		
	public T value() { return (T) DOMHandle.getVariableValueByProperty(this, "value", Object.class, "value"); }
	
	public void disabled(boolean value) { DOMHandle.setProperty(this, "disabled", value); }
	
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }
}
