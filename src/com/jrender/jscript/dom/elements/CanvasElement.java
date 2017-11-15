package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class CanvasElement extends Element {
		
	protected CanvasElement(Window window) { super(window, "canvas"); }

	public void height(Integer pixels) { DOMHandle.setProperty(this, "height", pixels); }	
	public Integer height() { return DOMHandle.getVariableValueByProperty(this, "height", Integer.class, "height"); }
	
	public void width(Integer width) { DOMHandle.setProperty(this, "width", width); }	
	public Integer width() { return DOMHandle.getVariableValueByProperty(this, "width", Integer.class, "width"); }
	
	public static CanvasElement cast(Element e) { return ElementHandle.cast(e, CanvasElement.class); }
}
