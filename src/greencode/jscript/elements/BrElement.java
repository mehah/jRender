package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class BrElement extends Element {		
	protected BrElement(Window window) { super(window, "br"); }
	
	public static BrElement cast(Element e) { return ElementHandle.cast(e, BrElement.class); }
}
