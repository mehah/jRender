package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Not supported in HTML5.*/
@Deprecated
public class BigElement extends Element {		
	protected BigElement(Window window) { super(window, "big"); }
	
	public static BigElement cast(Element e) { return ElementHandle.cast(e, BigElement.class); }
}
