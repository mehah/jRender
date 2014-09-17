package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class TableElement extends Element {		
	protected TableElement(Window window) { super(window, "table"); }
	
	public static TableElement cast(Element e) { return ElementHandle.cast(e, TableElement.class); }
}
