package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class H4Element extends Element {		
	protected H4Element(Window window) { super(window, "h4"); }
	
	public static H4Element cast(Element e) { return ElementHandle.cast(e, H4Element.class); }
}
