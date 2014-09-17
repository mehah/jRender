package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class StrongElement extends Element {		
	protected StrongElement(Window window) { super(window, "strong"); }
	
	public static StrongElement cast(Element e) { return ElementHandle.cast(e, StrongElement.class); }
}
