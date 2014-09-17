package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class KbdElement extends Element {		
	protected KbdElement(Window window) { super(window, "kbd"); }
	
	public static KbdElement cast(Element e) { return ElementHandle.cast(e, KbdElement.class); }
}
