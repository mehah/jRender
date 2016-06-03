package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class SpanElement extends Element {		
	protected SpanElement(Window window) { super(window, "span"); }
	
	public static SpanElement cast(Element e) { return ElementHandle.cast(e, SpanElement.class); }
}
