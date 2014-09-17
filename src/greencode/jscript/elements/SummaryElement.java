package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class SummaryElement extends Element {		
	protected SummaryElement(Window window) { super(window, "summary"); }
	
	public static SummaryElement cast(Element e) { return ElementHandle.cast(e, SummaryElement.class); }
}
