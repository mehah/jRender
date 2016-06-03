package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TDElement extends Element {		
	protected TDElement(Window window) { super(window, "td"); }
	
	public static TDElement cast(Element e) { return ElementHandle.cast(e, TDElement.class); }
}
