package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Not supported in HTML5.*/
public class StrikeElement extends Element {		
	protected StrikeElement(Window window) { super(window, "small"); }
	
	public static StrikeElement cast(Element e) { return ElementHandle.cast(e, StrikeElement.class); }
}
