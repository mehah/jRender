package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TitleElement extends Element {		
	protected TitleElement(Window window) { super(window, "title"); }
	
	public static TitleElement cast(Element e) { return ElementHandle.cast(e, TitleElement.class); }
}
