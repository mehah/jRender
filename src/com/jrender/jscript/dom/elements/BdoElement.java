package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class BdoElement extends Element {
		
	protected BdoElement(Window window) { super(window, "bdo"); }
	
	public void dir(String dir) { DOMHandle.setProperty(this, "dir", dir); }	
	public String dir() { return DOMHandle.getVariableValueByProperty(this, "dir", String.class, "dir"); }
	
	public static BdoElement cast(Element e) { return ElementHandle.cast(e, BdoElement.class); }
}
