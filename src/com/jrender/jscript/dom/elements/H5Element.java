package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class H5Element extends Element {		
	protected H5Element(Window window) { super(window, "h5"); }
	
	public static H5Element cast(Element e) { return ElementHandle.cast(e, H5Element.class); }
}
