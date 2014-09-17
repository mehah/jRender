package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class BdiElement extends Element {		
	protected BdiElement(Window window) { super(window, "bdi"); }
	
	public static BdiElement cast(Element e) { return ElementHandle.cast(e, BdiElement.class); }
}
