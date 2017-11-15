package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class StrongElement extends Element {		
	protected StrongElement(Window window) { super(window, "strong"); }
	
	public static StrongElement cast(Element e) { return ElementHandle.cast(e, StrongElement.class); }
}
