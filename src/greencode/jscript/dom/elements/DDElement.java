package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class DDElement extends Element {		
	protected DDElement(Window window) { super(window, "dd"); }
	
	public static DDElement cast(Element e) { return ElementHandle.cast(e, DDElement.class); }
}
