package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class SummaryElement extends Element {		
	protected SummaryElement(Window window) { super(window, "summary"); }
	
	public static SummaryElement cast(Element e) { return ElementHandle.cast(e, SummaryElement.class); }
}
