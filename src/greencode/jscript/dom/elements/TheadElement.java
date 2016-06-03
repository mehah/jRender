package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TheadElement extends Element {		
	protected TheadElement(Window window) { super(window, "thead"); }
	
	public static TheadElement cast(Element e) { return ElementHandle.cast(e, TheadElement.class); }
}
