package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Not supported in HTML5.*/
@Deprecated
public class BigElement extends Element {		
	protected BigElement(Window window) { super(window, "big"); }
	
	public static BigElement cast(Element e) { return ElementHandle.cast(e, BigElement.class); }
}
