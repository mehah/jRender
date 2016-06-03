package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H5Element extends Element {		
	protected H5Element(Window window) { super(window, "h5"); }
	
	public static H5Element cast(Element e) { return ElementHandle.cast(e, H5Element.class); }
}
