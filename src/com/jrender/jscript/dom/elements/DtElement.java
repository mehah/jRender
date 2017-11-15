package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class DtElement extends Element {		
	protected DtElement(Window window) { super(window, "dt"); }
	
	public static DtElement cast(Element e) { return ElementHandle.cast(e, DtElement.class); }
}
