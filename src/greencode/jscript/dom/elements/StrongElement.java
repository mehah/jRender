package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class StrongElement extends Element {		
	protected StrongElement(Window window) { super(window, "strong"); }
	
	public static StrongElement cast(Element e) { return ElementHandle.cast(e, StrongElement.class); }
}
