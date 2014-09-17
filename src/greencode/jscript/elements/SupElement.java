package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class SupElement extends Element {		
	protected SupElement(Window window) { super(window, "sup"); }
	
	public static SupElement cast(Element e) { return ElementHandle.cast(e, SupElement.class); }
}
