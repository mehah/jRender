package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H2Element extends Element {		
	protected H2Element(Window window) { super(window, "h2"); }
	
	public static H2Element cast(Element e) { return ElementHandle.cast(e, H2Element.class); }
}
