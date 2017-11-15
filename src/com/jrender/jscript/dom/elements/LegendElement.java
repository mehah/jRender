package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class LegendElement extends Element {		
	protected LegendElement(Window window) { super(window, "legend"); }
	
	public static LegendElement cast(Element e) { return ElementHandle.cast(e, LegendElement.class); }
}
