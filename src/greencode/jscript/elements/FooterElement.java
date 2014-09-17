package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class FooterElement extends Element {		
	protected FooterElement(Window window) { super(window, "footer"); }
	
	public static FooterElement cast(Element e) { return ElementHandle.cast(e, FooterElement.class); }
}
