package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class HeadElement extends Element {	
	protected HeadElement(Window window) { super(window, "head"); }
	
	public static HeadElement cast(Element e) { return ElementHandle.cast(e, HeadElement.class); }
}
