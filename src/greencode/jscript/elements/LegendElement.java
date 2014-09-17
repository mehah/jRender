package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class LegendElement extends Element {		
	protected LegendElement(Window window) { super(window, "legend"); }
	
	public static LegendElement cast(Element e) { return ElementHandle.cast(e, LegendElement.class); }
}
