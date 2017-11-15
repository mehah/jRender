package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TDElement extends Element {		
	protected TDElement(Window window) { super(window, "td"); }
	
	public static TDElement cast(Element e) { return ElementHandle.cast(e, TDElement.class); }
}
