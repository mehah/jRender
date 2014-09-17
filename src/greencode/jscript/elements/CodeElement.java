package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class CodeElement extends Element {		
	protected CodeElement(Window window) { super(window, "code"); }
	
	public static CodeElement cast(Element e) { return ElementHandle.cast(e, CodeElement.class); }
}
