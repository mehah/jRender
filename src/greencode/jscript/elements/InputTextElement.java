package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputTextElement<T> extends InputElementTextField<T> {
	protected InputTextElement(Window window) {
		super("text", window);
	}

	private InputTextElement(Window window, Class<?> typeValue) {
		super("text", window, typeValue);
	}

	public static <T> InputTextElement<T> cast(Element e) {
		return ElementHandle.cast(e, InputTextElement.class);
	}
}
