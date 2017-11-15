package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class FigureElement extends Element {		
	protected FigureElement(Window window) { super(window, "figure"); }
	
	public static FigureElement cast(Element e) { return ElementHandle.cast(e, FigureElement.class); }
}
