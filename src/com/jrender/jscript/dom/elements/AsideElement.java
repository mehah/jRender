package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class AsideElement extends Element {		
	protected AsideElement(Window window) { super(window, "aside"); }
	
	public static AsideElement cast(Element e) { return ElementHandle.cast(e, AsideElement.class); }
}
