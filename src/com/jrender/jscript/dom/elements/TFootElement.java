package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TFootElement extends Element {		
	protected TFootElement(Window window) { super(window, "tfoot"); }
	
	public static TFootElement cast(Element e) { return ElementHandle.cast(e, TFootElement.class); }
}
