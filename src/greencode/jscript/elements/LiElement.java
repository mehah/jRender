package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class LiElement extends Element {
		
	protected LiElement(Window window) { super(window, "li"); }
	
	public void value(int value) { DOMHandle.setProperty(this, "value", value); }
	public Integer value() { return DOMHandle.getVariableValueByProperty(this, "value", Integer.class, "value"); }
	
	public static LiElement cast(Element e) { return ElementHandle.cast(e, LiElement.class); }
}
