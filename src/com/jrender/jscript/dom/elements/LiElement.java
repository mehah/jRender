package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class LiElement extends Element {
		
	protected LiElement(Window window) { super(window, "li"); }
	
	public void value(int value) { DOMHandle.setProperty(this, "value", value); }
	public Integer value() { return DOMHandle.getVariableValueByProperty(this, "value", Integer.class, "value"); }
	
	public static LiElement cast(Element e) { return ElementHandle.cast(e, LiElement.class); }
}
