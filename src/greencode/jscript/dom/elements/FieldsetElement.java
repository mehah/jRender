package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class FieldsetElement extends Element {
		
	protected FieldsetElement(Window window) { super(window, "fieldset"); }
	
	/**Only supported in HTML5.*/
	public void disabled(boolean disabled) { DOMHandle.setProperty(this, "disabled", disabled); }
	/**Only supported in HTML5.*/
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }
	
	/**Only supported in HTML5.*/
	public void form(String formId) { DOMHandle.setProperty(this, "form", formId); }
	/**Only supported in HTML5.*/
	public String form() { return DOMHandle.getVariableValueByProperty(this, "form", String.class, "form"); }
	
	/**Only supported in HTML5.*/
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	/**Only supported in HTML5.*/
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public static FieldsetElement cast(Element e) { return ElementHandle.cast(e, FieldsetElement.class); }
}
