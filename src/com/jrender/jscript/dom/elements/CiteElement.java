package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class CiteElement extends Element {		
	protected CiteElement(Window window) { super(window, "cite"); }
	
	public static CiteElement cast(Element e) { return ElementHandle.cast(e, CiteElement.class); }
}
