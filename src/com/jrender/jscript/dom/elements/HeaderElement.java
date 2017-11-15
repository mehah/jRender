package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class HeaderElement extends Element {	
	protected HeaderElement(Window window) { super(window, "header"); }
	
	public static HeaderElement cast(Element e) { return ElementHandle.cast(e, HeaderElement.class); }
}
