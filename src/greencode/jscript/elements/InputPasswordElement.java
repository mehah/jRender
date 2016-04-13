package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

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
