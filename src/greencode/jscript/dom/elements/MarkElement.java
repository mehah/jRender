package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class MarkElement extends Element {		
	protected MarkElement(Window window) { super(window, "mark"); }
	
	public static MarkElement cast(Element e) { return ElementHandle.cast(e, MarkElement.class); }
}
