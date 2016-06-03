package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class DfnElement extends Element {		
	protected DfnElement(Window window) { super(window, "dfn"); }
	
	public static DfnElement cast(Element e) { return ElementHandle.cast(e, DfnElement.class); }
}
