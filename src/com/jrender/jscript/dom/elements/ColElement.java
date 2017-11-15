package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class ColElement extends Element {		
	protected ColElement(Window window) { super(window, "col"); }
	
	public static ColElement cast(Element e) { return ElementHandle.cast(e, ColElement.class); }
}
