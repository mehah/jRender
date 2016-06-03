package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TitleElement extends Element {		
	protected TitleElement(Window window) { super(window, "title"); }
	
	public static TitleElement cast(Element e) { return ElementHandle.cast(e, TitleElement.class); }
}
