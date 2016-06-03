package greencode.jscript.dom.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class BlockquoteElement extends Element {
		
	protected BlockquoteElement(Window window) { super(window, "blockquote"); }
	
	public void cite(String URL) { DOMHandle.setProperty(this, "cite", URL); }	
	public String cite() { return DOMHandle.getVariableValueByProperty(this, "cite", String.class, "cite"); }
	
	public static BlockquoteElement cast(Element e) { return ElementHandle.cast(e, BlockquoteElement.class); }
}
