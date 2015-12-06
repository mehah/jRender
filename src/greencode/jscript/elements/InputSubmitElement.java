package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputSubmitElement extends InputElementDisabling<String> {
	protected InputSubmitElement(Window window) {
		super("submit", window);
	}

	public static InputSubmitElement cast(Element e) {
		return ElementHandle.cast(e, InputSubmitElement.class);
	}
}
