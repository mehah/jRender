package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class NoScriptElement extends Element {
	protected NoScriptElement(Window window) { super(window, "noscript"); }
	
	public static NoScriptElement cast(Element e) { return ElementHandle.cast(e, NoScriptElement.class); }
}
