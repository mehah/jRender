package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class DatalistElement extends Element {		
	protected DatalistElement(Window window) { super(window, "datalist"); }
	
	public static DatalistElement cast(Element e) { return ElementHandle.cast(e, DatalistElement.class); }
}
