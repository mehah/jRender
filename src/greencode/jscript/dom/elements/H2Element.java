package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H2Element extends Element {		
	protected H2Element(Window window) { super(window, "h2"); }
	
	public static H2Element cast(Element e) { return ElementHandle.cast(e, H2Element.class); }
}
