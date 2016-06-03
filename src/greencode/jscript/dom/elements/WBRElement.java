package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class WBRElement extends Element {		
	protected WBRElement(Window window) { super(window, "wbr"); }
	
	public static WBRElement cast(Element e) { return ElementHandle.cast(e, WBRElement.class); }
}
