package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class FigcaptionElement extends Element {		
	protected FigcaptionElement(Window window) { super(window, "figcaption"); }
	
	public static FigcaptionElement cast(Element e) { return ElementHandle.cast(e, FigcaptionElement.class); }
}
