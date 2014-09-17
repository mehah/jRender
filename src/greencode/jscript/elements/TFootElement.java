package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class TFootElement extends Element {		
	protected TFootElement(Window window) { super(window, "tfoot"); }
	
	public static TFootElement cast(Element e) { return ElementHandle.cast(e, TFootElement.class); }
}
