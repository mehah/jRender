package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class BdiElement extends Element {		
	protected BdiElement(Window window) { super(window, "bdi"); }
	
	public static BdiElement cast(Element e) { return ElementHandle.cast(e, BdiElement.class); }
}
