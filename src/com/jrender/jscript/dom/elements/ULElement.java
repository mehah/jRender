package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class ULElement extends Element {		
	protected ULElement(Window window) { super(window, "ul"); }
	
	public static ULElement cast(Element e) { return ElementHandle.cast(e, ULElement.class); }
}
