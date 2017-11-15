package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class TimeElement extends Element {		
	protected TimeElement(Window window) { super(window, "time"); }
	
	public void datetime(String datetime) { DOMHandle.setProperty(this, "datetime", datetime); }	
	public String datetime() { return DOMHandle.getVariableValueByProperty(this, "datetime", String.class, "datetime"); }
	
	public static TimeElement cast(Element e) { return ElementHandle.cast(e, TimeElement.class); }
}
