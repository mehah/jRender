package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

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
