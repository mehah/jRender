package com.jrender.jscript.dom.elements;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class BlockquoteElement extends Element {
		
	protected BlockquoteElement(Window window) { super(window, "blockquote"); }
	
	public void cite(String URL) { DOMHandle.setProperty(this, "cite", URL); }	
	public String cite() { return DOMHandle.getVariableValueByProperty(this, "cite", String.class, "cite"); }
	
	public static BlockquoteElement cast(Element e) { return ElementHandle.cast(e, BlockquoteElement.class); }
}
