package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class SelectElement extends SelectElementPrototype {
	protected SelectElement() { super("select-one"); }
	protected SelectElement(Window window) { super("select-one", window); }
	
	public static SelectElement cast(Element e) { return ElementHandle.cast(e, SelectElement.class); }
	
	/**
	 * CUSTOM METHOD
	 * @return String
	 */
	public String selectedValue() {
		return DOMHandle.getVariableValue(this, "selectedValue", String.class);
	}
	
	/**
	 * CUSTOM METHOD
	 */
	public void selectedValue(String value) {
		DOMHandle.setVariableValue(this, "selectedValue", value);
		DOMHandle.CustomMethod.call(this, "selectOptionByValue", value);
	}
}
