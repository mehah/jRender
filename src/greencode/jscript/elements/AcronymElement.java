package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Not supported in HTML5.*/
@Deprecated
public class AcronymElement extends Element {		
	protected AcronymElement(Window window) { super(window, "acronym"); }
	
	public static AcronymElement cast(Element e) { return ElementHandle.cast(e, AcronymElement.class); }
}
