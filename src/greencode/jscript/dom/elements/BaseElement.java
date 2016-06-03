package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class BaseElement extends Element {
		
	protected BaseElement(Window window) { super(window, "base"); }
	
	public void href(String url) { DOMHandle.setProperty(this, "href", url); }	
	public String href() { return DOMHandle.getVariableValueByProperty(this, "href", String.class, "href"); }
	
	public void target(String target) { DOMHandle.setProperty(this, "target", target); }	
	public String target() { return DOMHandle.getVariableValueByProperty(this, "target", String.class, "target"); }
	
	public static BaseElement cast(Element e) { return ElementHandle.cast(e, BaseElement.class); }
}
