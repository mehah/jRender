package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class FigureElement extends Element {		
	protected FigureElement(Window window) { super(window, "figure"); }
	
	public static FigureElement cast(Element e) { return ElementHandle.cast(e, FigureElement.class); }
}
