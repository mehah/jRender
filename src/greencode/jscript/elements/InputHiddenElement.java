package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputHiddenElement extends InputElement {
	protected InputHiddenElement(Window window) { super("hidden", window); }
	
	public static InputHiddenElement cast(Element e) { return ElementHandle.cast(e, InputHiddenElement.class); }
}
