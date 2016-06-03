package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Not supported in HTML5.*/
@Deprecated
public class BigElement extends Element {		
	protected BigElement(Window window) { super(window, "big"); }
	
	public static BigElement cast(Element e) { return ElementHandle.cast(e, BigElement.class); }
}
