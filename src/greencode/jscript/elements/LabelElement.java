package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class LabelElement extends Element {
		
	protected LabelElement(Window window) { super(window, "label"); }
	
	public void For(String elementId) { DOMHandle.setProperty(this, "for", elementId); }
	public String For() { return DOMHandle.getVariableValueByProperty(this, "for", String.class, "for"); }

	/**Only supported in HTML5.*/
	public void form(String formId) { DOMHandle.setProperty(this, "form", formId); }
	/**Only supported in HTML5.*/
	public String form() { return DOMHandle.getVariableValueByProperty(this, "form", String.class, "form"); }
	
	public static LabelElement cast(Element e) { return ElementHandle.cast(e, LabelElement.class); }	
}
