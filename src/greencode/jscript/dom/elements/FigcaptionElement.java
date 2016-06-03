package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class FigcaptionElement extends Element {		
	protected FigcaptionElement(Window window) { super(window, "figcaption"); }
	
	public static FigcaptionElement cast(Element e) { return ElementHandle.cast(e, FigcaptionElement.class); }
}
