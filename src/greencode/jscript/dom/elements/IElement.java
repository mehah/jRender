package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class IElement extends Element {	
	protected IElement(Window window) { super(window, "i"); }
	
	public static IElement cast(Element e) { return ElementHandle.cast(e, IElement.class); }
}
