package greencode.jscript.dom.elements;

import greencode.exception.GreencodeError;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Window;
import greencode.util.LogMessage;

public class SelectMultipleElement<T> extends SelectElementPrototype<T> {

	protected SelectMultipleElement(Window window) {
		this(window, null);
	}
	
	protected SelectMultipleElement(Window window, Class<?> typeValue) {
		super("select-multiple", window, typeValue);
		
		if(this.typeValue.isArray())
			throw new GreencodeError(LogMessage.getMessage("green-0050", getClass().getSimpleName()));
	}

	// CUSTOM METHOD
	public T[] selectedValues() {
		return (T[]) DOMHandle.getVariableValue(this, "value", this.typeValue);
	}
}
