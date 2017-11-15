package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InsElement extends Element {
		
	protected InsElement(Window window) { super(window, "ins"); }
	
	public void cite(String URL) { DOMHandle.setProperty(this, "cite", URL); }	
	public String cite() { return DOMHandle.getVariableValueByProperty(this, "cite", String.class, "cite"); }
	
	public void datetime(String datetime) { DOMHandle.setProperty(this, "datetime", datetime); }	
	public String datetime() { return DOMHandle.getVariableValueByProperty(this, "datetime", String.class, "datetime"); }
	
	public static InsElement cast(Element e) { return ElementHandle.cast(e, InsElement.class); }
}
