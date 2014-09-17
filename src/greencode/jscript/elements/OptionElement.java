package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Form;
import greencode.jscript.Window;

public class OptionElement extends Element {
	
	protected OptionElement(Window window) { super(window, "option"); }
	
	public Form form() { return null; }
	
	public void text(String text) { DOMHandle.setProperty(this, "text", text); }
	
	public String text() { return DOMHandle.getVariableValueByProperty(this, "text", String.class, "text"); }
	
	public String value() { return DOMHandle.getVariableValueByProperty(this, "value", String.class, "value"); }
	
	public void value(String value) { DOMHandle.setProperty(this, "value", value); }
	
	public Integer index() { return DOMHandle.getVariableValueByProperty(this, "index", Integer.class, "index"); }
	
	public void index(Integer index) { DOMHandle.setProperty(this, "index", index); }
	
	public Boolean selected() { return DOMHandle.getVariableValueByProperty(this, "selected", Boolean.class, "selected"); }
	
	public void selected(Boolean selected) { DOMHandle.setProperty(this, "selected", selected); }
	
	public void disabled(Boolean disabled) { DOMHandle.setProperty(this, "disabled", disabled); }
	
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }
	
	public Boolean defaultSelected() { return DOMHandle.getVariableValueByProperty(this, "defaultSelected", Boolean.class, "defaultSelected"); }
	
	public static OptionElement cast(Element e) { return ElementHandle.cast(e, OptionElement.class); }
}
