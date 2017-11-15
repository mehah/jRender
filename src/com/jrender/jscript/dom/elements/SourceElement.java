package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

/**Only supported in HTML5.*/
public class SourceElement extends Element {
		
	protected SourceElement(Window window) { super(window, "source"); }	
	
	public void media(String mediaQuery) { DOMHandle.setProperty(this, "media", mediaQuery); }
	public String media() { return DOMHandle.getVariableValueByProperty(this, "media", String.class, "media"); }

	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	public void src(String url) { DOMHandle.setProperty(this, "src", url); }
	
	public void type(String type) { DOMHandle.setProperty(this, "type", type); }
	public String type() { return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type"); }
	
	public static SourceElement cast(Element e) { return ElementHandle.cast(e, SourceElement.class); }
}
