package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class DtElement extends Element {		
	protected DtElement(Window window) { super(window, "dt"); }
	
	public static DtElement cast(Element e) { return ElementHandle.cast(e, DtElement.class); }
}
