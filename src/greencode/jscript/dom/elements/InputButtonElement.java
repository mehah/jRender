package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class InputButtonElement extends InputElementDisabling<String> {
	protected InputButtonElement(Window window) { super("button", window); }
	
	public static InputButtonElement cast(Element e) { return ElementHandle.cast(e, InputButtonElement.class); }
}
