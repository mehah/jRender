package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class FooterElement extends Element {		
	protected FooterElement(Window window) { super(window, "footer"); }
	
	public static FooterElement cast(Element e) { return ElementHandle.cast(e, FooterElement.class); }
}
