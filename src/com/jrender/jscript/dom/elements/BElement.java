package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class BElement extends Element {		
	protected BElement(Window window) { super(window, "b"); }
	
	public static BElement cast(Element e) { return ElementHandle.cast(e, BElement.class); }
}
