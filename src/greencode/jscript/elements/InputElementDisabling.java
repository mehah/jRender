package greencode.jscript.elements;

import greencode.jscript.DOMHandle;
import greencode.jscript.Window;

public abstract class InputElementDisabling<T> extends InputElement<T> {

	protected InputElementDisabling(String type, Window window) {
		super(type, window);
	}

	protected InputElementDisabling(String type, Window window, Class<?> typeValue) {
		super(type, window, typeValue);
	}

	public void disabled(Boolean disabled) {
		DOMHandle.setProperty(this, "disabled", disabled);
	}

	public Boolean disabled() {
		return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled");
	}
}
