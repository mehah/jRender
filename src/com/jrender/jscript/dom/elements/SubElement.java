package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class SubElement extends Element {		
	protected SubElement(Window window) { super(window, "sub"); }
	
	public static SubElement cast(Element e) { return ElementHandle.cast(e, SubElement.class); }
}
