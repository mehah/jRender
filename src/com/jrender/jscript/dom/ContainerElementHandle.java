package com.jrender.jscript.dom;

import java.lang.reflect.Field;

import com.jrender.exception.JRenderError;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.elements.InputFileElement;
import com.jrender.jscript.dom.elements.InputHiddenElement;
import com.jrender.jscript.dom.elements.InputPasswordElement;
import com.jrender.jscript.dom.elements.InputRadioElement;
import com.jrender.jscript.dom.elements.InputTextElement;
import com.jrender.jscript.dom.elements.SelectElement;
import com.jrender.jscript.dom.elements.SelectMultipleElement;
import com.jrender.jscript.dom.elements.TextareaElement;
import com.jrender.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import com.jrender.jscript.dom.form.annotation.ElementValue;

public final class ContainerElementHandle {
	public static Object getValueByName(ContainerElementImplementation container, String name) {
		Field[] fields = com.jrender.jscript.dom.$Container.getElementFields(container);
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
			throw new JRenderError(e);
		}
	}
}
