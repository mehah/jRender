package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class DlElement extends Element {		
	protected DlElement(Window window) { super(window, "dl"); }
	
	public static DlElement cast(Element e) { return ElementHandle.cast(e, DlElement.class); }
}
