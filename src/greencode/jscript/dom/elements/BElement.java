package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class BElement extends Element {		
	protected BElement(Window window) { super(window, "b"); }
	
	public static BElement cast(Element e) { return ElementHandle.cast(e, BElement.class); }
}
