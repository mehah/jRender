package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class DfnElement extends Element {		
	protected DfnElement(Window window) { super(window, "dfn"); }
	
	public static DfnElement cast(Element e) { return ElementHandle.cast(e, DfnElement.class); }
}
