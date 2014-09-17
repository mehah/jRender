package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class HrElement extends Element {	
	protected HrElement(Window window) { super(window, "hr"); }
	
	public static HrElement cast(Element e) { return ElementHandle.cast(e, HrElement.class); }
}
