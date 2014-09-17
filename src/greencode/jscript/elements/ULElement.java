package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class ULElement extends Element {		
	protected ULElement(Window window) { super(window, "ul"); }
	
	public static ULElement cast(Element e) { return ElementHandle.cast(e, ULElement.class); }
}
