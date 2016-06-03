package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class LiElement extends Element {
		
	protected LiElement(Window window) { super(window, "li"); }
	
	public void value(int value) { DOMHandle.setProperty(this, "value", value); }
	public Integer value() { return DOMHandle.getVariableValueByProperty(this, "value", Integer.class, "value"); }
	
	public static LiElement cast(Element e) { return ElementHandle.cast(e, LiElement.class); }
}
