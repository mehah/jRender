package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class TableElement extends Element {		
	protected TableElement(Window window) { super(window, "table"); }
	
	public static TableElement cast(Element e) { return ElementHandle.cast(e, TableElement.class); }
}
