package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class IElement extends Element {	
	protected IElement(Window window) { super(window, "i"); }
	
	public static IElement cast(Element e) { return ElementHandle.cast(e, IElement.class); }
}
