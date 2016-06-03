package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class ColgroupElement extends Element {		
	protected ColgroupElement(Window window) { super(window, "colgroup"); }
	
	public static ColgroupElement cast(Element e) { return ElementHandle.cast(e, ColgroupElement.class); }
}
