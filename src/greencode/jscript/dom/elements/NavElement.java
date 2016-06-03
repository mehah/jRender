package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class NavElement extends Element {
	protected NavElement(Window window) { super(window, "nav"); }
	
	public static NavElement cast(Element e) { return ElementHandle.cast(e, NavElement.class); }
}
