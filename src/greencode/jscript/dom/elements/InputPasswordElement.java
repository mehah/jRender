package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class InputPasswordElement extends InputElementTextField<String> {
	protected InputPasswordElement(Window window) {
		super("password", window);
	}
	
	private InputPasswordElement(Window window, Class<?> typeValue) {
		super("password", window, typeValue);
	}

	public static InputPasswordElement cast(Element e) {
		return ElementHandle.cast(e, InputPasswordElement.class);
	}
}
