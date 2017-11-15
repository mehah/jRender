package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class NavElement extends Element {
	protected NavElement(Window window) { super(window, "nav"); }
	
	public static NavElement cast(Element e) { return ElementHandle.cast(e, NavElement.class); }
}
