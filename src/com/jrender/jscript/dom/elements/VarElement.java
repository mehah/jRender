package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class VarElement extends Element {		
	protected VarElement(Window window) { super(window, "var"); }
	
	public static VarElement cast(Element e) { return ElementHandle.cast(e, VarElement.class); }
}
