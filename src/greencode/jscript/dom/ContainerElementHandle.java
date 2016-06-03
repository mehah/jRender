package greencode.jscript.dom;

import java.lang.reflect.Field;

import greencode.exception.GreencodeError;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.elements.InputFileElement;
import greencode.jscript.dom.elements.InputHiddenElement;
import greencode.jscript.dom.elements.InputPasswordElement;
import greencode.jscript.dom.elements.InputRadioElement;
import greencode.jscript.dom.elements.InputTextElement;
import greencode.jscript.dom.elements.SelectElement;
import greencode.jscript.dom.elements.SelectMultipleElement;
import greencode.jscript.dom.elements.TextareaElement;
import greencode.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.dom.form.annotation.ElementValue;

public final class ContainerElementHandle {
	public static Object getValueByName(ContainerElementImplementation container, String name) {
		Field[] fields = greencode.jscript.dom.$Container.getElementFields(container);
		try {
			for(Field f: fields) {
				ElementValue anno = f.getAnnotation(ElementValue.class);
				if(anno.name().equals(name) || f.getName().equals(name)) {
					Class<?> fieldType = f.getType();
					Object value = f.get(container);
					if(fieldType.equals(TextareaElement.class) || fieldType.equals(InputTextElement.class) || fieldType.equals(InputRadioElement.class) || fieldType.equals(InputPasswordElement.class) || fieldType.equals(InputHiddenElement.class) || fieldType.equals(InputFileElement.class) || fieldType.equals(SelectElement.class) || fieldType.equals(SelectMultipleElement.class))
						return DOMHandle.getVariableValue((Element) value, "value", null);

					return f.get(f.get(container));
				}
			}
			
			return null;
		} catch(Exception e) {
			throw new GreencodeError(e);
		}
	}
}
