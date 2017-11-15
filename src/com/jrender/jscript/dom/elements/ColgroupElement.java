package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class ColgroupElement extends Element {		
	protected ColgroupElement(Window window) { super(window, "colgroup"); }
	
	public static ColgroupElement cast(Element e) { return ElementHandle.cast(e, ColgroupElement.class); }
}
