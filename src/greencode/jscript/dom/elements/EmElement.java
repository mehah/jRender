package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class EmElement extends Element {		
	protected EmElement(Window window) { super(window, "em"); }
	
	public static EmElement cast(Element e) { return ElementHandle.cast(e, EmElement.class); }
}
