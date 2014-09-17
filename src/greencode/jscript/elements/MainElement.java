package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class MainElement extends Element {	
	protected MainElement(Window window) { super(window, "main"); }
	
	public static MainElement cast(Element e) { return ElementHandle.cast(e, MainElement.class); }
}
