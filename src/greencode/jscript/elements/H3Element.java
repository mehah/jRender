package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class H3Element extends Element {		
	protected H3Element(Window window) { super(window, "h3"); }
	
	public static H3Element cast(Element e) { return ElementHandle.cast(e, H3Element.class); }
}
