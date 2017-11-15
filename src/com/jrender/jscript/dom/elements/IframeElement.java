package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.elements.attribute.SandboxAttr;

/**Only supported in HTML5.*/
public class IframeElement extends Element {
		
	protected IframeElement(Window window) { super(window, "iframe"); }

	public void height(Integer pixels) { DOMHandle.setProperty(this, "height", pixels); }	
	public Integer height() { return DOMHandle.getVariableValueByProperty(this, "height", Integer.class, "height"); }
	
	public void name(String name) { DOMHandle.setProperty(this, "name", name); }	
	public String name() { return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name"); }
	
	/**Only supported in HTML5.*/
	public void sandbox(SandboxAttr sandbox) { DOMHandle.setProperty(this, "sandbox", sandbox.value); }
	/**Only supported in HTML5.*/
	public SandboxAttr sandbox() { return SandboxAttr.getByValue(DOMHandle.getVariableValueByProperty(this, "sandbox", String.class, "sandbox")); }
	
	/**Only supported in HTML5.*/
	public void seamless(boolean seamless) { DOMHandle.setProperty(this, "seamless", seamless); }
	/**Only supported in HTML5.*/
	public Boolean seamless() { return DOMHandle.getVariableValueByProperty(this, "seamless", Boolean.class, "seamless"); }
	
	public void src(String URL) { DOMHandle.setProperty(this, "src", URL); }	
	public String src() { return DOMHandle.getVariableValueByProperty(this, "src", String.class, "src"); }
	
	public void srcdoc(String htmlCode) { DOMHandle.setProperty(this, "srcdoc", htmlCode); }	
	public String srcdoc() { return DOMHandle.getVariableValueByProperty(this, "srcdoc", String.class, "srcdoc"); }
	
	public void width(int width) { DOMHandle.setProperty(this, "width", width); }	
	public Integer width() { return DOMHandle.getVariableValueByProperty(this, "width", Integer.class, "width"); }
	
	public static IframeElement cast(Element e) { return ElementHandle.cast(e, IframeElement.class); }
}
