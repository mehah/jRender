package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class CaptionElement extends Element {		
	protected CaptionElement(Window window) { super(window, "caption"); }
	
	public static CaptionElement cast(Element e) { return ElementHandle.cast(e, CaptionElement.class); }
}
