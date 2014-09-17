package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class BodyElement extends Element {	
	protected BodyElement(Window window) { super(window, "body"); }
	
	public static BodyElement cast(Element e) { return ElementHandle.cast(e, BodyElement.class); }
}
