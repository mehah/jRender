package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class UElement extends Element {		
	protected UElement(Window window) { super(window, "u"); }
	
	public static UElement cast(Element e) { return ElementHandle.cast(e, UElement.class); }
}
