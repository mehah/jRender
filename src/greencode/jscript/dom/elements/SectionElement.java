package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class SectionElement extends Element {
	protected SectionElement(Window window) { super(window, "section"); }
	
	public static SectionElement cast(Element e) { return ElementHandle.cast(e, SectionElement.class); }
}
