package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class FigureElement extends Element {		
	protected FigureElement(Window window) { super(window, "figure"); }
	
	public static FigureElement cast(Element e) { return ElementHandle.cast(e, FigureElement.class); }
}
