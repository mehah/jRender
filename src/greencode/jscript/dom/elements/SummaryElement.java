package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class SummaryElement extends Element {		
	protected SummaryElement(Window window) { super(window, "summary"); }
	
	public static SummaryElement cast(Element e) { return ElementHandle.cast(e, SummaryElement.class); }
}
