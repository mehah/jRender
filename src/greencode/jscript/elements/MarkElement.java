package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class MarkElement extends Element {		
	protected MarkElement(Window window) { super(window, "mark"); }
	
	public static MarkElement cast(Element e) { return ElementHandle.cast(e, MarkElement.class); }
}
