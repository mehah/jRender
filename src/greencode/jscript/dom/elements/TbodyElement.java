package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TbodyElement extends Element {		
	protected TbodyElement(Window window) { super(window, "tbody"); }
	
	public static TbodyElement cast(Element e) { return ElementHandle.cast(e, TbodyElement.class); }
}
