package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class IElement extends Element {	
	protected IElement(Window window) { super(window, "i"); }
	
	public static IElement cast(Element e) { return ElementHandle.cast(e, IElement.class); }
}
