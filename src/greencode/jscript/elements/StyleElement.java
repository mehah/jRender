package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class StyleElement extends Element {
		
	protected StyleElement(Window window) { super(window, "style"); }	
	
	public void media(String mediaQuery) { DOMHandle.setProperty(this, "media", mediaQuery); }
	public String media() { return DOMHandle.getVariableValueByProperty(this, "media", String.class, "media"); }

	public void type(String type) { DOMHandle.setProperty(this, "type", type); }
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public static StyleElement cast(Element e) { return ElementHandle.cast(e, StyleElement.class); }
}
