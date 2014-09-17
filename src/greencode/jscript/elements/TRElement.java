package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class TRElement extends Element {		
	protected TRElement(Window window) { super(window, "tr"); }
	
	public static TRElement cast(Element e) { return ElementHandle.cast(e, TRElement.class); }
}
