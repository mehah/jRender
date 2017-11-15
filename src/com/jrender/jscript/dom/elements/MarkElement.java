package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class MarkElement extends Element {		
	protected MarkElement(Window window) { super(window, "mark"); }
	
	public static MarkElement cast(Element e) { return ElementHandle.cast(e, MarkElement.class); }
}
