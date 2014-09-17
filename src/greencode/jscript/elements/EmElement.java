package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class EmElement extends Element {		
	protected EmElement(Window window) { super(window, "em"); }
	
	public static EmElement cast(Element e) { return ElementHandle.cast(e, EmElement.class); }
}
