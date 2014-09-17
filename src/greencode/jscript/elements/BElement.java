package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class BElement extends Element {		
	protected BElement(Window window) { super(window, "b"); }
	
	public static BElement cast(Element e) { return ElementHandle.cast(e, BElement.class); }
}
