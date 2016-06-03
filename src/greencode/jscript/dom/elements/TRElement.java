package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TRElement extends Element {		
	protected TRElement(Window window) { super(window, "tr"); }
	
	public static TRElement cast(Element e) { return ElementHandle.cast(e, TRElement.class); }
}
