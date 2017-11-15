package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class DatalistElement extends Element {		
	protected DatalistElement(Window window) { super(window, "datalist"); }
	
	public static DatalistElement cast(Element e) { return ElementHandle.cast(e, DatalistElement.class); }
}
