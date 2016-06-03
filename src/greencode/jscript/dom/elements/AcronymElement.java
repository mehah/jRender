package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Not supported in HTML5.*/
@Deprecated
public class AcronymElement extends Element {		
	protected AcronymElement(Window window) { super(window, "acronym"); }
	
	public static AcronymElement cast(Element e) { return ElementHandle.cast(e, AcronymElement.class); }
}
