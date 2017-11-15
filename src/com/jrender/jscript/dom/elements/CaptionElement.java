package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class CaptionElement extends Element {		
	protected CaptionElement(Window window) { super(window, "caption"); }
	
	public static CaptionElement cast(Element e) { return ElementHandle.cast(e, CaptionElement.class); }
}
