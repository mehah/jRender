package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class KbdElement extends Element {		
	protected KbdElement(Window window) { super(window, "kbd"); }
	
	public static KbdElement cast(Element e) { return ElementHandle.cast(e, KbdElement.class); }
}
