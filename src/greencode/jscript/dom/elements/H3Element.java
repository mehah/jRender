package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H3Element extends Element {		
	protected H3Element(Window window) { super(window, "h3"); }
	
	public static H3Element cast(Element e) { return ElementHandle.cast(e, H3Element.class); }
}
