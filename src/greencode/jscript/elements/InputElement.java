package greencode.jscript.elements;

import java.lang.reflect.ParameterizedType;

import greencode.exception.GreencodeError;
import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.Form;
import greencode.jscript.Window;
import greencode.jscript.WindowHandle;
import greencode.kernel.LogMessage;
import greencode.util.ClassUtils;

public abstract class InputElement<T> extends Element {
	private final Class<T> typeValue;

	protected InputElement(String type, Window window) {
		this(type, window, null);
	}

	protected InputElement(String type, Window window, Class<?> typeValue) {
		super(window, "input");
		DOMHandle.setVariableValue(this, "type", type);

		Class<?> classUnnamed = this.getClass();
		if (classUnnamed.getGenericSuperclass() instanceof Class)
			throw new GreencodeError(LogMessage.getMessage("green-0046", ((Class<?>) classUnnamed.getGenericSuperclass()).getSimpleName()));

		this.typeValue = (Class<T>) (typeValue == null ? ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] : typeValue);
	}

	public Form form() {
		return null;
	}

	public void name(String name) {
		DOMHandle.setProperty(this, "name", name);
	}

	public String name() {
		return DOMHandle.getVariableValueByProperty(this, "name", String.class, "name");
	}

	public void type(String type) {
		DOMHandle.setProperty(this, "type", type);
	}

	public String type() {
		return DOMHandle.getVariableValueByProperty(this, "type", String.class, "type");
	}

	public void value(T value) {
		if(!ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
			WindowHandle.registerObjectParamter(window, value);
		}
		
		DOMHandle.setProperty(this, "value", value.toString());
	}

	public T value() {
		return DOMHandle.getVariableValueByProperty(this, "value", typeValue, "value");
	}

	public void disabled(boolean value) {
		DOMHandle.setProperty(this, "disabled", value);
	}

	public Boolean disabled() {
		return DOMHandle.getVariableValueByProperty(this, "disabled", Boolean.class, "disabled");
	}
}
