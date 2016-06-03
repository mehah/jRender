package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class MapElement extends Element {
		
	protected MapElement(Window window) { super(window, "map"); }
	
	public void name(String mapname) { DOMHandle.setProperty(this, "name", mapname); }	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public static MapElement cast(Element e) { return ElementHandle.cast(e, MapElement.class); }
}
