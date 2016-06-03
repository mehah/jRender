package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class CiteElement extends Element {		
	protected CiteElement(Window window) { super(window, "cite"); }
	
	public static CiteElement cast(Element e) { return ElementHandle.cast(e, CiteElement.class); }
}
