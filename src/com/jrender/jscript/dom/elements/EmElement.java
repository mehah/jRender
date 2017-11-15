package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class EmElement extends Element {		
	protected EmElement(Window window) { super(window, "em"); }
	
	public static EmElement cast(Element e) { return ElementHandle.cast(e, EmElement.class); }
}
