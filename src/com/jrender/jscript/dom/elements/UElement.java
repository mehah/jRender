package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class UElement extends Element {		
	protected UElement(Window window) { super(window, "u"); }
	
	public static UElement cast(Element e) { return ElementHandle.cast(e, UElement.class); }
}
