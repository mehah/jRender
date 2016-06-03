package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class SupElement extends Element {		
	protected SupElement(Window window) { super(window, "sup"); }
	
	public static SupElement cast(Element e) { return ElementHandle.cast(e, SupElement.class); }
}
