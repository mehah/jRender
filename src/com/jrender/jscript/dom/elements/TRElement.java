package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TRElement extends Element {		
	protected TRElement(Window window) { super(window, "tr"); }
	
	public static TRElement cast(Element e) { return ElementHandle.cast(e, TRElement.class); }
}
