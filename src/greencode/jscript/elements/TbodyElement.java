package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class TbodyElement extends Element {		
	protected TbodyElement(Window window) { super(window, "tbody"); }
	
	public static TbodyElement cast(Element e) { return ElementHandle.cast(e, TbodyElement.class); }
}
