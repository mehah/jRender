package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class VarElement extends Element {		
	protected VarElement(Window window) { super(window, "var"); }
	
	public static VarElement cast(Element e) { return ElementHandle.cast(e, VarElement.class); }
}
