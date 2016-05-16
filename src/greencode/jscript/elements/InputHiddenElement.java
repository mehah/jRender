package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputHiddenElement<T> extends InputElement<T> {
	protected InputHiddenElement(Window window) {
		super("hidden", window);
	}

	private InputHiddenElement(Window window, Class<?> typeValue) {
		super("hidden", window, typeValue);
	}

	public static<T> InputHiddenElement<String> cast(Element e) {
		return ElementHandle.cast(e, InputHiddenElement.class);
	}
	
	public static<T> InputHiddenElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, InputHiddenElement.class, type);
	}
}
