package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class InputSubmitElement extends InputElementDisabling<String> {
	protected InputSubmitElement(Window window) {
		super("submit", window);
	}

	public static InputSubmitElement cast(Element e) {
		return ElementHandle.cast(e, InputSubmitElement.class);
	}
}
