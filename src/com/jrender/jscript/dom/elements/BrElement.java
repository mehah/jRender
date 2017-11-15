package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class BrElement extends Element {		
	protected BrElement(Window window) { super(window, "br"); }
	
	public static BrElement cast(Element e) { return ElementHandle.cast(e, BrElement.class); }
}
