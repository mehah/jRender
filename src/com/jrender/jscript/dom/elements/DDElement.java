package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class DDElement extends Element {		
	protected DDElement(Window window) { super(window, "dd"); }
	
	public static DDElement cast(Element e) { return ElementHandle.cast(e, DDElement.class); }
}
