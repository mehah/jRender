package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

/**Only supported in HTML5.*/
public class DialogElement extends Element {
		
	protected DialogElement(Window window) { super(window, "dialog"); }
	
	public void open(Boolean open) { DOMHandle.setProperty(this, "open", open); }	
	public Boolean open() { return DOMHandle.getVariableValueByProperty(this, "open", Boolean.class, "open"); }
	
	public static DialogElement cast(Element e) { return ElementHandle.cast(e, DialogElement.class); }
}
