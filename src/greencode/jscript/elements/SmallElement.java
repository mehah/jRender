package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class SmallElement extends Element {		
	protected SmallElement(Window window) { super(window, "small"); }
	
	public static SmallElement cast(Element e) { return ElementHandle.cast(e, SmallElement.class); }
}
