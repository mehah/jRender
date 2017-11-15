package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class AbbrElement extends Element {		
	protected AbbrElement(Window window) { super(window, "abbr"); }
	
	public static AbbrElement cast(Element e) { return ElementHandle.cast(e, AbbrElement.class); }
}
