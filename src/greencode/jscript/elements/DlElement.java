package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class DlElement extends Element {		
	protected DlElement(Window window) { super(window, "dl"); }
	
	public static DlElement cast(Element e) { return ElementHandle.cast(e, DlElement.class); }
}
