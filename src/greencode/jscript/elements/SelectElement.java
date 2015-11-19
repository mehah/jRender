package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class SelectElement<T> extends SelectElementPrototype {
	protected SelectElement() { super("select-one"); }
	protected SelectElement(Window window) { super("select-one", window); }
	
	public static SelectElement cast(Element e) { return ElementHandle.cast(e, SelectElement.class); }
	
	/**
	 * CUSTOM METHOD
	 * @return String
	 */
	public T selectedValue() {
		return (T) DOMHandle.getVariableValue(this, "selectedValue", Object.class);
	}
	
	/**
	 * CUSTOM METHOD
	 */
	public void selectedValue(T value) {
		DOMHandle.setVariableValue(this, "selectedValue", value);
		DOMHandle.CustomMethod.call(this, "selectOptionByValue", value);
	}
}
