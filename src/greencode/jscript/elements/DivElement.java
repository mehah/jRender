package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class DivElement extends Element {		
	protected DivElement(Window window) { super(window, "div"); }
	
	public static DivElement cast(Element e) { return ElementHandle.cast(e, DivElement.class); }
}
