package greencode.jscript.elements;

import java.lang.reflect.ParameterizedType;

import greencode.exception.GreencodeError;
import greencode.jscript.DOMHandle;
import greencode.jscript.Window;
import greencode.kernel.LogMessage;

public class SelectMultipleElement<T> extends SelectElementPrototype {
	private final Class<T> typeValue;

	protected SelectMultipleElement(Window window) {
		this(window, null);
	}
	
	protected SelectMultipleElement(Window window, Class<?> typeValue) {
		super("select-multiple", window);
		
		this.typeValue = (Class<T>) (typeValue == null ?  ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] : typeValue);
		
		if(!this.typeValue.isArray())
			throw new GreencodeError(LogMessage.getMessage("green-0050", getClass().getSimpleName()));
	}

	// CUSTOM METHOD
	public T[] selectedValues() {
		return (T[]) DOMHandle.getVariableValue(this, "value", this.typeValue);
	}
}
