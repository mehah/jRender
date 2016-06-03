package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class MainElement extends Element {	
	protected MainElement(Window window) { super(window, "main"); }
	
	public static MainElement cast(Element e) { return ElementHandle.cast(e, MainElement.class); }
}
