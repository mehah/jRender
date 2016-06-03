package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H1Element extends Element {		
	protected H1Element(Window window) { super(window, "h1"); }
	
	public static H1Element cast(Element e) { return ElementHandle.cast(e, H1Element.class); }
}
