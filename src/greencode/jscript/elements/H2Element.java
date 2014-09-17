package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class H2Element extends Element {		
	protected H2Element(Window window) { super(window, "h2"); }
	
	public static H2Element cast(Element e) { return ElementHandle.cast(e, H2Element.class); }
}
