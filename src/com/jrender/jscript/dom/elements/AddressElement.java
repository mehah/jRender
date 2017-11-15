package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class AddressElement extends Element {		
	protected AddressElement(Window window) { super(window, "address"); }
	
	public static AddressElement cast(Element e) { return ElementHandle.cast(e, AddressElement.class); }
}
