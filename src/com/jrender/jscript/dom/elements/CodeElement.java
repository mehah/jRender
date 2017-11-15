package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class CodeElement extends Element {		
	protected CodeElement(Window window) { super(window, "code"); }
	
	public static CodeElement cast(Element e) { return ElementHandle.cast(e, CodeElement.class); }
}
