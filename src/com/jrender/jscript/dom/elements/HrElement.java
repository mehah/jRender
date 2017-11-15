package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class HrElement extends Element {	
	protected HrElement(Window window) { super(window, "hr"); }
	
	public static HrElement cast(Element e) { return ElementHandle.cast(e, HrElement.class); }
}
