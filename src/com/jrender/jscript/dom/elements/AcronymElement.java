package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Not supported in HTML5.*/
@Deprecated
public class AcronymElement extends Element {		
	protected AcronymElement(Window window) { super(window, "acronym"); }
	
	public static AcronymElement cast(Element e) { return ElementHandle.cast(e, AcronymElement.class); }
}
