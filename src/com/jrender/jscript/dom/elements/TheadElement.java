package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TheadElement extends Element {		
	protected TheadElement(Window window) { super(window, "thead"); }
	
	public static TheadElement cast(Element e) { return ElementHandle.cast(e, TheadElement.class); }
}
