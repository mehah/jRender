package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class DDElement extends Element {		
	protected DDElement(Window window) { super(window, "dd"); }
	
	public static DDElement cast(Element e) { return ElementHandle.cast(e, DDElement.class); }
}
