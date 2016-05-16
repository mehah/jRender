package greencode.jscript.elements;

import java.lang.reflect.ParameterizedType;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class SelectElement<T> extends SelectElementPrototype {
	private final Class<T> typeValue;

	protected SelectElement(Window window) {
		this(window, null);
	}
	
	protected SelectElement(Window window, Class<?> typeValue) {
		super("select-one", window);
		
		this.typeValue = (Class<T>) (typeValue == null ?  ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] : typeValue);
	}

	public static SelectElement<String> cast(Element e) {
		return ElementHandle.cast(e, SelectElement.class);
	}
	
	public static<T> SelectElement<T> cast(Element e, Class<T> type) {
		return ElementHandle.cast(e, SelectElement.class, type);
	}

	/**
	 * CUSTOM METHOD
	 * 
	 * @return String
	 */
	public T selectedValue() {
		return (T) DOMHandle.getVariableValue(this, "value", typeValue);
	}

	/**
	 * CUSTOM METHOD
	 */
	public void selectedValue(T value) {
		DOMHandle.setVariableValue(this, "value", value);
		DOMHandle.CustomMethod.call(this, "selectOptionByValue", value);
	}
}
