package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Window;

public class SelectMultipleElement<T> extends SelectElementPrototype {
	protected SelectMultipleElement() { super("select-multiple"); }
	protected SelectMultipleElement(Window window) { super("select-multiple", window); }
	
	// CUSTOM METHOD
	public T[] selectedValues() {
		return (T[]) DOMHandle.getVariableValue(this, "selectedValues", Object[].class);
	}
}
