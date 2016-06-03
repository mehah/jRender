package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class CaptionElement extends Element {		
	protected CaptionElement(Window window) { super(window, "caption"); }
	
	public static CaptionElement cast(Element e) { return ElementHandle.cast(e, CaptionElement.class); }
}
