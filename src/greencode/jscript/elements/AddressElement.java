package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class AddressElement extends Element {		
	protected AddressElement(Window window) { super(window, "address"); }
	
	public static AddressElement cast(Element e) { return ElementHandle.cast(e, AddressElement.class); }
}
