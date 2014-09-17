package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class AbbrElement extends Element {		
	protected AbbrElement(Window window) { super(window, "abbr"); }
	
	public static AbbrElement cast(Element e) { return ElementHandle.cast(e, AbbrElement.class); }
}
