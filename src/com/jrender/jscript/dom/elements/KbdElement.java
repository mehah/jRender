package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class KbdElement extends Element {		
	protected KbdElement(Window window) { super(window, "kbd"); }
	
	public static KbdElement cast(Element e) { return ElementHandle.cast(e, KbdElement.class); }
}
