package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class HtmlElement extends Element {
		
	protected HtmlElement(Window window) { super(window, "html"); }
	
	public void manifest(String URL) { DOMHandle.setProperty(this, "manifest", URL); }	
	public String manifest() { return DOMHandle.getVariableValueByProperty(this, "manifest", String.class, "manifest"); }
	
	public void xmlns(String URL) { DOMHandle.setProperty(this, "xmlns", URL); }	
	public String xmlns() { return DOMHandle.getVariableValueByProperty(this, "xmlns", String.class, "xmlns"); }
	
	public static HtmlElement cast(Element e) { return ElementHandle.cast(e, HtmlElement.class); }
}
