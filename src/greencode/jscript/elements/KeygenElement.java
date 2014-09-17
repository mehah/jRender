package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;
import greencode.jscript.elements.attribute.KeytypeAttr;

/**Only supported in HTML5.*/
public class KeygenElement extends Element {
		
	protected KeygenElement(Window window) { super(window, "keygen"); }
	
	public void autofocus(boolean autofocus) { DOMHandle.setProperty(this, "autofocus", autofocus); }
	public Boolean autofocus() { return DOMHandle.getVariableValueByProperty(this, "autofocus", Boolean.class, "autofocus"); }
	
	public void challenge(String challenge) { DOMHandle.setProperty(this, "challenge", challenge); }
	public String challenge() { return DOMHandle.getVariableValueByProperty(this, "challenge", String.class, "challenge"); }
	
	public void disabled(boolean disabled) { DOMHandle.setProperty(this, "disabled", disabled); }
	public Boolean disabled() { return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled"); }
	
	public void form(String formId) { DOMHandle.setProperty(this, "form", formId); }
	public String form() { return DOMHandle.getVariableValueByProperty(this, "form", String.class, "form"); }
	
	public void keytype(KeytypeAttr keytype) { DOMHandle.setProperty(this, "keytype", keytype); }
	public KeytypeAttr keytype() { return KeytypeAttr.valueOf(DOMHandle.getVariableValueByProperty(this, "keytype", String.class, "keytype").toUpperCase()); }
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public static KeygenElement cast(Element e) { return ElementHandle.cast(e, KeygenElement.class); }
}
