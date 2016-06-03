package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class ColElement extends Element {		
	protected ColElement(Window window) { super(window, "col"); }
	
	public static ColElement cast(Element e) { return ElementHandle.cast(e, ColElement.class); }
}
