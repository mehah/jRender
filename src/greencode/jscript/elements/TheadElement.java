package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class TheadElement extends Element {		
	protected TheadElement(Window window) { super(window, "thead"); }
	
	public static TheadElement cast(Element e) { return ElementHandle.cast(e, TheadElement.class); }
}
