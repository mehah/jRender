package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class DialogElement extends Element {
		
	protected DialogElement(Window window) { super(window, "dialog"); }
	
	public void open(Boolean open) { DOMHandle.setProperty(this, "open", open); }	
	public Boolean open() { return DOMHandle.getVariableValueByProperty(this, "open", Boolean.class, "open"); }
	
	public static DialogElement cast(Element e) { return ElementHandle.cast(e, DialogElement.class); }
}
