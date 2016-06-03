package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Not supported in HTML5.*/
public class StrikeElement extends Element {		
	protected StrikeElement(Window window) { super(window, "small"); }
	
	public static StrikeElement cast(Element e) { return ElementHandle.cast(e, StrikeElement.class); }
}
