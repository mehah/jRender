package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class BdoElement extends Element {
		
	protected BdoElement(Window window) { super(window, "bdo"); }
	
	public void dir(String dir) { DOMHandle.setProperty(this, "dir", dir); }	
	public String dir() { return DOMHandle.getVariableValueByProperty(this, "dir", String.class, "dir"); }
	
	public static BdoElement cast(Element e) { return ElementHandle.cast(e, BdoElement.class); }
}
