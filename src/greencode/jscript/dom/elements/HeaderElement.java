package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class HeaderElement extends Element {	
	protected HeaderElement(Window window) { super(window, "header"); }
	
	public static HeaderElement cast(Element e) { return ElementHandle.cast(e, HeaderElement.class); }
}
