package greencode.jscript.dom.elements;

import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class InputRadioElement<T> extends InputElementCheckable<T> {
	protected InputRadioElement(Window window) {
		super("radio", window);
	}

	private InputRadioElement(Window window, Class<?> typeValue) {
		super("radio", window, typeValue);
	}

	public static InputRadioElement<String> cast(Element e) {
		return ElementHandle.cast(e, InputRadioElement.class);
	}
	
	public static<T> InputRadioElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, InputRadioElement.class, type);
	}
}
