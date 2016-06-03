package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class BodyElement extends Element {	
	protected BodyElement(Window window) { super(window, "body"); }
	
	public static BodyElement cast(Element e) { return ElementHandle.cast(e, BodyElement.class); }
}
