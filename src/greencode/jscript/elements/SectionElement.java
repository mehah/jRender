package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class SectionElement extends Element {
	protected SectionElement(Window window) { super(window, "section"); }
	
	public static SectionElement cast(Element e) { return ElementHandle.cast(e, SectionElement.class); }
}
