package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H1Element extends Element {		
	protected H1Element(Window window) { super(window, "h1"); }
	
	public static H1Element cast(Element e) { return ElementHandle.cast(e, H1Element.class); }
}
