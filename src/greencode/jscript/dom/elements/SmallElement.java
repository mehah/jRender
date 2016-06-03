package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class SmallElement extends Element {		
	protected SmallElement(Window window) { super(window, "small"); }
	
	public static SmallElement cast(Element e) { return ElementHandle.cast(e, SmallElement.class); }
}
