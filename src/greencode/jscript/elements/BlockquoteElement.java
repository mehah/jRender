package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class BlockquoteElement extends Element {
		
	protected BlockquoteElement(Window window) { super(window, "blockquote"); }
	
	public void cite(String URL) { DOMHandle.setProperty(this, "cite", URL); }	
	public String cite() { return DOMHandle.getVariableValueByProperty(this, "cite", String.class, "cite"); }
	
	public static BlockquoteElement cast(Element e) { return ElementHandle.cast(e, BlockquoteElement.class); }
}
