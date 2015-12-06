package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputCheckboxElement<T> extends InputElementCheckable<T> {
	protected InputCheckboxElement(Window window) {
		super("checkbox", window);
	}

	private InputCheckboxElement(Window window, Class<?> typeValue) {
		super("checkbox", window, typeValue);
	}

	public static InputCheckboxElement cast(Element e) {
		return ElementHandle.cast(e, InputCheckboxElement.class);
	}
}
