package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputButtonElement extends InputElementDisabling {
	protected InputButtonElement(Window window) { super("button", window); }
	
	public static InputButtonElement cast(Element e) { return ElementHandle.cast(e, InputButtonElement.class); }
}
