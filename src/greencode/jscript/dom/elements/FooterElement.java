package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class FooterElement extends Element {		
	protected FooterElement(Window window) { super(window, "footer"); }
	
	public static FooterElement cast(Element e) { return ElementHandle.cast(e, FooterElement.class); }
}
