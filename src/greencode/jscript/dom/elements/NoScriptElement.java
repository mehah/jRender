package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class NoScriptElement extends Element {
	protected NoScriptElement(Window window) { super(window, "noscript"); }
	
	public static NoScriptElement cast(Element e) { return ElementHandle.cast(e, NoScriptElement.class); }
}
