package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class DfnElement extends Element {		
	protected DfnElement(Window window) { super(window, "dfn"); }
	
	public static DfnElement cast(Element e) { return ElementHandle.cast(e, DfnElement.class); }
}
