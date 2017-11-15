package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class SectionElement extends Element {
	protected SectionElement(Window window) { super(window, "section"); }
	
	public static SectionElement cast(Element e) { return ElementHandle.cast(e, SectionElement.class); }
}
