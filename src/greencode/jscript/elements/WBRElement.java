package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class WBRElement extends Element {		
	protected WBRElement(Window window) { super(window, "wbr"); }
	
	public static WBRElement cast(Element e) { return ElementHandle.cast(e, WBRElement.class); }
}
