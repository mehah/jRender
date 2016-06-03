package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class AsideElement extends Element {		
	protected AsideElement(Window window) { super(window, "aside"); }
	
	public static AsideElement cast(Element e) { return ElementHandle.cast(e, AsideElement.class); }
}
