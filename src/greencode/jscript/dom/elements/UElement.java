package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class UElement extends Element {		
	protected UElement(Window window) { super(window, "u"); }
	
	public static UElement cast(Element e) { return ElementHandle.cast(e, UElement.class); }
}
