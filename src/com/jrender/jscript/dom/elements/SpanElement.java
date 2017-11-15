package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class SpanElement extends Element {		
	protected SpanElement(Window window) { super(window, "span"); }
	
	public static SpanElement cast(Element e) { return ElementHandle.cast(e, SpanElement.class); }
}
