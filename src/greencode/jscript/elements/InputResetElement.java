package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputResetElement extends InputElementDisabling<String> {
	protected InputResetElement(Window window) {
		super("reset", window);
	}

	public static InputResetElement cast(Element e) {
		return ElementHandle.cast(e, InputResetElement.class);
	}
}
