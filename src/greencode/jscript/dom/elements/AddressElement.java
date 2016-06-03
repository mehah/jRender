package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class AddressElement extends Element {		
	protected AddressElement(Window window) { super(window, "address"); }
	
	public static AddressElement cast(Element e) { return ElementHandle.cast(e, AddressElement.class); }
}
