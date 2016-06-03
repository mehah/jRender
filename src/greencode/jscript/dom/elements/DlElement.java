package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class DlElement extends Element {		
	protected DlElement(Window window) { super(window, "dl"); }
	
	public static DlElement cast(Element e) { return ElementHandle.cast(e, DlElement.class); }
}
