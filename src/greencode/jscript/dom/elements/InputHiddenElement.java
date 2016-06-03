package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class InputHiddenElement<T> extends InputElement<T> {
	protected InputHiddenElement(Window window) {
		super("hidden", window);
	}

	private InputHiddenElement(Window window, Class<?> typeValue) {
		super("hidden", window, typeValue);
	}

	public static InputHiddenElement<String> cast(Element e) {
		return ElementHandle.cast(e, InputHiddenElement.class);
	}
	
	public static<T> InputHiddenElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, InputHiddenElement.class, type);
	}
}
