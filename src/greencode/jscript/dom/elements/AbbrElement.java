package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class AbbrElement extends Element {		
	protected AbbrElement(Window window) { super(window, "abbr"); }
	
	public static AbbrElement cast(Element e) { return ElementHandle.cast(e, AbbrElement.class); }
}
