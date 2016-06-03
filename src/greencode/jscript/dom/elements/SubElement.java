package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class SubElement extends Element {		
	protected SubElement(Window window) { super(window, "sub"); }
	
	public static SubElement cast(Element e) { return ElementHandle.cast(e, SubElement.class); }
}
