package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputTextElement extends InputElementTextField {
	protected InputTextElement(Window window) { super("text", window); }
	
	public static InputTextElement cast(Element e) { return ElementHandle.cast(e, InputTextElement.class); }
}
