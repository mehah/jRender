package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class LegendElement extends Element {		
	protected LegendElement(Window window) { super(window, "legend"); }
	
	public static LegendElement cast(Element e) { return ElementHandle.cast(e, LegendElement.class); }
}
