package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class CodeElement extends Element {		
	protected CodeElement(Window window) { super(window, "code"); }
	
	public static CodeElement cast(Element e) { return ElementHandle.cast(e, CodeElement.class); }
}
