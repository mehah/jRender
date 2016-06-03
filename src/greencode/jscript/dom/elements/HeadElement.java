package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class HeadElement extends Element {	
	protected HeadElement(Window window) { super(window, "head"); }
	
	public static HeadElement cast(Element e) { return ElementHandle.cast(e, HeadElement.class); }
}
