package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class MainElement extends Element {	
	protected MainElement(Window window) { super(window, "main"); }
	
	public static MainElement cast(Element e) { return ElementHandle.cast(e, MainElement.class); }
}
