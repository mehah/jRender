package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class FigcaptionElement extends Element {		
	protected FigcaptionElement(Window window) { super(window, "figcaption"); }
	
	public static FigcaptionElement cast(Element e) { return ElementHandle.cast(e, FigcaptionElement.class); }
}
