package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class InputButtonElement extends InputElementDisabling<String> {
	protected InputButtonElement(Window window) { super("button", window); }
	
	public static InputButtonElement cast(Element e) { return ElementHandle.cast(e, InputButtonElement.class); }
}
