package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class DivElement extends Element {		
	protected DivElement(Window window) { super(window, "div"); }
	
	public static DivElement cast(Element e) { return ElementHandle.cast(e, DivElement.class); }
}
