package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TFootElement extends Element {		
	protected TFootElement(Window window) { super(window, "tfoot"); }
	
	public static TFootElement cast(Element e) { return ElementHandle.cast(e, TFootElement.class); }
}
