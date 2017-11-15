package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class BodyElement extends Element {	
	protected BodyElement(Window window) { super(window, "body"); }
	
	public static BodyElement cast(Element e) { return ElementHandle.cast(e, BodyElement.class); }
}
