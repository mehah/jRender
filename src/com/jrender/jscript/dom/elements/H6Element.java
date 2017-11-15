package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H6Element extends Element {		
	protected H6Element(Window window) { super(window, "h6"); }
	
	public static H6Element cast(Element e) { return ElementHandle.cast(e, H6Element.class); }
}
