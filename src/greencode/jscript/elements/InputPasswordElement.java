package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputPasswordElement extends InputElementTextField {
	protected InputPasswordElement(Window window) { super("password", window); }
	
	public static InputPasswordElement cast(Element e) { return ElementHandle.cast(e, InputPasswordElement.class); }
}
