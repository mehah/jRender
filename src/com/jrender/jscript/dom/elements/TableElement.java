package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TableElement extends Element {		
	protected TableElement(Window window) { super(window, "table"); }
	
	public static TableElement cast(Element e) { return ElementHandle.cast(e, TableElement.class); }
}
