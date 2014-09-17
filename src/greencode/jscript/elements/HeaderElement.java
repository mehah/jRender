package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class HeaderElement extends Element {	
	protected HeaderElement(Window window) { super(window, "header"); }
	
	public static HeaderElement cast(Element e) { return ElementHandle.cast(e, HeaderElement.class); }
}
