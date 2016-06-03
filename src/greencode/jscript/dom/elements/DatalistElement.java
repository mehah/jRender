package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class DatalistElement extends Element {		
	protected DatalistElement(Window window) { super(window, "datalist"); }
	
	public static DatalistElement cast(Element e) { return ElementHandle.cast(e, DatalistElement.class); }
}
