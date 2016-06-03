package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class ULElement extends Element {		
	protected ULElement(Window window) { super(window, "ul"); }
	
	public static ULElement cast(Element e) { return ElementHandle.cast(e, ULElement.class); }
}
