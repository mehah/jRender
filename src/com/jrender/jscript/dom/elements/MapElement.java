package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class MapElement extends Element {
		
	protected MapElement(Window window) { super(window, "map"); }
	
	public void name(String mapname) { DOMHandle.setProperty(this, "name", mapname); }	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	public static MapElement cast(Element e) { return ElementHandle.cast(e, MapElement.class); }
}
