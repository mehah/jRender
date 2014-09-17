package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class ColElement extends Element {		
	protected ColElement(Window window) { super(window, "col"); }
	
	public static ColElement cast(Element e) { return ElementHandle.cast(e, ColElement.class); }
}
