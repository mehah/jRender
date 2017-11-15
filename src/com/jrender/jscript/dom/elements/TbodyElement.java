package com.jrender.jscript.dom.elements;

import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class TbodyElement extends Element {		
	protected TbodyElement(Window window) { super(window, "tbody"); }
	
	public static TbodyElement cast(Element e) { return ElementHandle.cast(e, TbodyElement.class); }
}
