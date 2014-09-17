package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class ColgroupElement extends Element {		
	protected ColgroupElement(Window window) { super(window, "colgroup"); }
	
	public static ColgroupElement cast(Element e) { return ElementHandle.cast(e, ColgroupElement.class); }
}
