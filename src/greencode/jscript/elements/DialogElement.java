package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

/**Only supported in HTML5.*/
public class DialogElement extends Element {
		
	protected DialogElement(Window window) { super(window, "dialog"); }
	
	public void open(Boolean open) { DOMHandle.setProperty(this, "open", open); }	
	public Boolean open() { return DOMHandle.getVariableValueByProperty(this, "open", Boolean.class, "open"); }
	
	public static DialogElement cast(Element e) { return ElementHandle.cast(e, DialogElement.class); }
}
