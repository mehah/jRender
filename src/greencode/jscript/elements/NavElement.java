package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class NavElement extends Element {
	protected NavElement(Window window) { super(window, "nav"); }
	
	public static NavElement cast(Element e) { return ElementHandle.cast(e, NavElement.class); }
}
