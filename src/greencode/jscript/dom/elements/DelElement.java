package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class DelElement extends Element {
		
	protected DelElement(Window window) { super(window, "del"); }
	
	public void cite(String URL) { DOMHandle.setProperty(this, "cite", URL); }	
	public String cite() { return DOMHandle.getVariableValueByProperty(this, "cite", String.class, "cite"); }
	
	public void datetime(String datetime) { DOMHandle.setProperty(this, "datetime", datetime); }	
	public String datetime() { return DOMHandle.getVariableValueByProperty(this, "datetime", String.class, "datetime"); }
	
	public static DelElement cast(Element e) { return ElementHandle.cast(e, DelElement.class); }
}
