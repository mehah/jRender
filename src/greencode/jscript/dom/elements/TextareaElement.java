package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;
import greencode.jscript.dom.elements.attribute.WrapAttr;

public class TextareaElement extends Element {
		
	protected TextareaElement(Window window, Class<?> typeValue) {
		this(window);
	}
	
	protected TextareaElement(Window window) { super(window, "textarea"); }

	/**Only supported in HTML5.*/
	public void autofocus(boolean autofocus) { if(!autofocus) removeAttribute("autofocus"); else DOMHandle.setProperty(this, "autofocus", "autofocus"); }	
	/**Only supported in HTML5.*/
	public Boolean autofocus() { return hasAttribute("autofocus"); }
	
	public void cols(int cols) { DOMHandle.setProperty(this, "cols", cols); }	
	public Integer cols() { return DOMHandle.getVariableValueByProperty(this, "cols", Integer.class, "cols"); }
	
	public void disabled(Boolean disabled) { DOMHandle.setProperty(this, "disabled", disabled); }	
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }

	/**Only supported in HTML5.*/
	public void form(String formId) { DOMHandle.setProperty(this, "form", formId); }	
	/**Only supported in HTML5.*/
	public String form() { return DOMHandle.getVariableValueByProperty(this, "form", String.class, "form"); }
	
	/**Only supported in HTML5.*/
	public void maxlength(int maxlength) { DOMHandle.setProperty(this, "maxlength", maxlength); }	
	/**Only supported in HTML5.*/
	public Integer maxlength() { return DOMHandle.getVariableValueByProperty(this, "maxlength", Integer.class, "maxlength"); }
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	/**Only supported in HTML5.*/
	public void placeholder(String placeholder) { DOMHandle.setProperty(this, "placeholder", placeholder); }	
	/**Only supported in HTML5.*/
	public String placeholder() { return DOMHandle.getVariableValueByProperty(this, "placeholder", String.class, "placeholder"); }
	
	public void readOnly(Boolean readOnly) { DOMHandle.setProperty(this, "readOnly", readOnly); }	
	public Boolean readOnly() { return DOMHandle.getVariableValueByProperty(this, "readOnly", Boolean.class, "readOnly"); }
	
	/**Only supported in HTML5.*/
	public void required(Boolean required) { DOMHandle.setProperty(this, "required", required); }	
	/**Only supported in HTML5.*/
	public Boolean required() { return DOMHandle.getVariableValueByProperty(this, "required", Boolean.class, "required"); }
	
	public void rows(int rows) { DOMHandle.setProperty(this, "rows", rows); }	
	public Integer rows() { return DOMHandle.getVariableValueByProperty(this, "rows", Integer.class, "rows"); }
	
	/**Only supported in HTML5.*/
	public WrapAttr wrap() { return WrapAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "wrap", String.class, "wrap").toUpperCase()); }
	/**Only supported in HTML5.*/
	public void wrap(WrapAttr wrap) { DOMHandle.setProperty(this, "wrap", wrap); }
	
	public void value(String value) { DOMHandle.setProperty(this, "value", value); }	
	public String value() { return DOMHandle.getVariableValueByProperty(this, "value", String.class, "value"); }
	
	public static TextareaElement cast(Element e) { return ElementHandle.cast(e, TextareaElement.class); }
}
