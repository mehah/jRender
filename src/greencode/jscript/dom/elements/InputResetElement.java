package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class InputResetElement extends InputElementDisabling<String> {
	protected InputResetElement(Window window) {
		super("reset", window);
	}

	public static InputResetElement cast(Element e) {
		return ElementHandle.cast(e, InputResetElement.class);
	}
}
