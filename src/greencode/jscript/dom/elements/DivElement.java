package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class DivElement extends Element {		
	protected DivElement(Window window) { super(window, "div"); }
	
	public static DivElement cast(Element e) { return ElementHandle.cast(e, DivElement.class); }
}
