package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class CiteElement extends Element {		
	protected CiteElement(Window window) { super(window, "cite"); }
	
	public static CiteElement cast(Element e) { return ElementHandle.cast(e, CiteElement.class); }
}
