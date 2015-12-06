package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputHiddenElement<T> extends InputElement<T> {
	protected InputHiddenElement(Window window) { super("hidden", window); }
	
	private InputHiddenElement(Window window, Class<?> typeValue) {
		super("hidden", window, typeValue);
	}
	
	public static InputHiddenElement cast(Element e) { return ElementHandle.cast(e, InputHiddenElement.class); }
}
