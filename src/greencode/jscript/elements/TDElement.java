package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class TDElement extends Element {		
	protected TDElement(Window window) { super(window, "td"); }
	
	public static TDElement cast(Element e) { return ElementHandle.cast(e, TDElement.class); }
}
