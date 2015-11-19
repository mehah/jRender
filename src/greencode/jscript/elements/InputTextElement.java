package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputTextElement<T> extends InputElementTextField<T> {
	protected InputTextElement(Window window) { super("text", window); }
	
	public static<T> InputTextElement<T> cast(Element e) {
		InputElementTextField.class.getName();
		return ElementHandle.cast(e, InputTextElement.class); }
}
