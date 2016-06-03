package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class DtElement extends Element {		
	protected DtElement(Window window) { super(window, "dt"); }
	
	public static DtElement cast(Element e) { return ElementHandle.cast(e, DtElement.class); }
}
