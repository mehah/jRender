package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class BdiElement extends Element {		
	protected BdiElement(Window window) { super(window, "bdi"); }
	
	public static BdiElement cast(Element e) { return ElementHandle.cast(e, BdiElement.class); }
}
