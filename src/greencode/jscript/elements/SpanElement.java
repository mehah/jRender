package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class SpanElement extends Element {		
	protected SpanElement(Window window) { super(window, "span"); }
	
	public static SpanElement cast(Element e) { return ElementHandle.cast(e, SpanElement.class); }
}
