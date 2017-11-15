package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class WBRElement extends Element {		
	protected WBRElement(Window window) { super(window, "wbr"); }
	
	public static WBRElement cast(Element e) { return ElementHandle.cast(e, WBRElement.class); }
}
