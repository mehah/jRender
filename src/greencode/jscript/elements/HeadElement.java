package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class HeadElement extends Element {	
	protected HeadElement(Window window) { super(window, "head"); }
	
	public static HeadElement cast(Element e) { return ElementHandle.cast(e, HeadElement.class); }
}
