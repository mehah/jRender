package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class AsideElement extends Element {		
	protected AsideElement(Window window) { super(window, "aside"); }
	
	public static AsideElement cast(Element e) { return ElementHandle.cast(e, AsideElement.class); }
}
