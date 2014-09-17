package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Not supported in HTML5.*/
public class StrikeElement extends Element {		
	protected StrikeElement(Window window) { super(window, "small"); }
	
	public static StrikeElement cast(Element e) { return ElementHandle.cast(e, StrikeElement.class); }
}
