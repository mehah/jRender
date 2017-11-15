package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H3Element extends Element {		
	protected H3Element(Window window) { super(window, "h3"); }
	
	public static H3Element cast(Element e) { return ElementHandle.cast(e, H3Element.class); }
}
