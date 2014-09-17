package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class VarElement extends Element {		
	protected VarElement(Window window) { super(window, "var"); }
	
	public static VarElement cast(Element e) { return ElementHandle.cast(e, VarElement.class); }
}
