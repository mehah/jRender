package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class NoScriptElement extends Element {
	protected NoScriptElement(Window window) { super(window, "noscript"); }
	
	public static NoScriptElement cast(Element e) { return ElementHandle.cast(e, NoScriptElement.class); }
}
