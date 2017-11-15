package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class SupElement extends Element {		
	protected SupElement(Window window) { super(window, "sup"); }
	
	public static SupElement cast(Element e) { return ElementHandle.cast(e, SupElement.class); }
}
