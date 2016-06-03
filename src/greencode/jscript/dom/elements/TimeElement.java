package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class TimeElement extends Element {		
	protected TimeElement(Window window) { super(window, "time"); }
	
	public void datetime(String datetime) { DOMHandle.setProperty(this, "datetime", datetime); }	
	public String datetime() { return DOMHandle.getVariableValueByProperty(this, "datetime", String.class, "datetime"); }
	
	public static TimeElement cast(Element e) { return ElementHandle.cast(e, TimeElement.class); }
}
