package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class BrElement extends Element {		
	protected BrElement(Window window) { super(window, "br"); }
	
	public static BrElement cast(Element e) { return ElementHandle.cast(e, BrElement.class); }
}
