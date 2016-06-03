package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Form;
import greencode.jscript.dom.Window;

public class ButtonElement extends Element {
		
	protected ButtonElement(Window window) { super(window, "button"); }

	/**Only supported in HTML5.*/
	public void autofocus(boolean autofocus) { DOMHandle.setProperty(this, "autofocus", autofocus); }
	/**Only supported in HTML5.*/
	public Boolean autofocus() { return DOMHandle.getVariableValueByProperty(this, "autofocus", Boolean.class, "autofocus"); }
	
	public void disabled(boolean disabled) { DOMHandle.setProperty(this, "disabled", disabled); }	
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }
	
	/**Only supported in HTML5.*/
	public Form form() { return null; }
	
	/**Only supported in HTML5.*/
	public void formAction(String URL) { DOMHandle.setProperty(this, "formaction", URL); }
	/**Only supported in HTML5.*/
	public String formAction() { return DOMHandle.getVariableValueByProperty(this, "formaction", String.class, "formaction"); }
	
	/**Only supported in HTML5.*/
	public void formEnctype(String formenctype) { DOMHandle.setProperty(this, "formenctype", formenctype); }
	/**Only supported in HTML5.*/
	public String formEnctype() { return DOMHandle.getVariableValueByProperty(this, "formenctype", String.class, "formenctype"); }
	
	/**Only supported in HTML5.*/
	public void formMethod(String formmethod) { DOMHandle.setProperty(this, "formmethod", formmethod); }
	/**Only supported in HTML5.*/
	public String formMethod() { return DOMHandle.getVariableValueByProperty(this, "formmethod", String.class, "formmethod"); }
	
	/**Only supported in HTML5.*/
	public void formNoValidate(boolean formnovalidate) { DOMHandle.setProperty(this, "formnovalidate", formnovalidate); }
	/**Only supported in HTML5.*/
	public Boolean formNoValidate() { return DOMHandle.getVariableValueByProperty(this, "formnovalidate", Boolean.class, "formnovalidate"); }
	
	/**Only supported in HTML5.*/
	public void formTarget(String formtarget) { DOMHandle.setProperty(this, "formtarget", formtarget); }
	/**Only supported in HTML5.*/
	public String formTarget() { return DOMHandle.getVariableValueByProperty(this, "formtarget", String.class, "formtarget"); }
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	public void type(String type) { DOMHandle.setProperty(this, "type", type); }
	
	public void value(String value) { DOMHandle.setProperty(this, "value", value); }	
	public String value() { return DOMHandle.getVariableValueByProperty(this, "value", String.class, "value"); }
	
	public static ButtonElement cast(Element e) { return ElementHandle.cast(e, ButtonElement.class); }
}
