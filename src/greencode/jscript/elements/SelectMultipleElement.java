package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Window;

public class SelectMultipleElement extends SelectElementPrototype {
	protected SelectMultipleElement() { super("select-multiple"); }
	protected SelectMultipleElement(Window window) { super("select-multiple", window); }
	
	// CUSTOM METHOD
	public String[] selectedValues() {
		return DOMHandle.getVariableValue(this, "selectedValues", String[].class);
	}
}
