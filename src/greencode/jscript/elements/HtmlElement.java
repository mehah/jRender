package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class HtmlElement extends Element {
		
	protected HtmlElement(Window window) { super(window, "html"); }
	
	public void manifest(String URL) { DOMHandle.setProperty(this, "manifest", URL); }	
	public String manifest() { return DOMHandle.getVariableValueByProperty(this, "manifest", String.class, "manifest"); }
	
	public void xmlns(String URL) { DOMHandle.setProperty(this, "xmlns", URL); }	
	public String xmlns() { return DOMHandle.getVariableValueByProperty(this, "xmlns", String.class, "xmlns"); }
	
	public static HtmlElement cast(Element e) { return ElementHandle.cast(e, HtmlElement.class); }
}
