package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class HrElement extends Element {	
	protected HrElement(Window window) { super(window, "hr"); }
	
	public static HrElement cast(Element e) { return ElementHandle.cast(e, HrElement.class); }
}
