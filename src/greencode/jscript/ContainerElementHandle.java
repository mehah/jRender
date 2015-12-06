package greencode.jscript;

import java.lang.reflect.Field;

import greencode.exception.GreencodeError;
import greencode.jscript.elements.InputFileElement;
import greencode.jscript.elements.InputHiddenElement;
import greencode.jscript.elements.InputPasswordElement;
import greencode.jscript.elements.InputRadioElement;
import greencode.jscript.elements.InputTextElement;
import greencode.jscript.elements.SelectElement;
import greencode.jscript.elements.SelectMultipleElement;
import greencode.jscript.elements.TextareaElement;
import greencode.jscript.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.form.annotation.ElementValue;

public final class ContainerElementHandle {
	public static Object getValueByName(ContainerElementImplementation container, String name) {
		Field[] fields = greencode.jscript.$Container.getElementFields(container);
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
