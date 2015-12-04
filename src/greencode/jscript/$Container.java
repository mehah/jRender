package greencode.jscript;

import greencode.exception.OperationNotAllowedException;
import greencode.jscript.elements.InputFileElement;
import greencode.jscript.elements.InputHiddenElement;
import greencode.jscript.elements.InputPasswordElement;
import greencode.jscript.elements.InputRadioElement;
import greencode.jscript.elements.InputTextElement;
import greencode.jscript.elements.SelectElement;
import greencode.jscript.elements.SelectMultipleElement;
import greencode.jscript.elements.TextareaElement;
import greencode.jscript.elements.custom.ContainerElement;
import greencode.jscript.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.form.annotation.ElementValue;
import greencode.kernel.LogMessage;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class $Container {
	private $Container() {
	}

	public static Field[] getElementFields(ContainerElementImplementation e) {
		if(e instanceof Form)
			return ((Form) e).elementFields;
		else if(e instanceof ContainerElement)
			return greencode.jscript.elements.custom.$Container.getElementFields((ContainerElement<?>) e);

		return null;
	}

	public static Field[] processFields(Class<? extends ContainerElementImplementation> currentClass) {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(currentClass, "container:elements");
		if(fields == null) {
			List<Field> fieldList = new ArrayList<Field>();

			Class<? extends ContainerElementImplementation>[] classes = (Class<? extends ContainerElementImplementation>[]) ClassUtils.getParents(currentClass, Form.class);

			int i = -1;
			do {
				for(Field field: GenericReflection.getDeclaredFields(currentClass)) {
					if(field.isAnnotationPresent(greencode.jscript.form.annotation.ElementValue.class)) {
						Class<?> type = field.getType();
						if(type.isArray())
							type = type.getComponentType();

						if(!(
							type.equals(Date.class) ||
							type.equals(Part.class) ||
							isAcceptableElement(type) ||
							ClassUtils.isPrimitiveOrWrapper(type) ||
							ClassUtils.isParent(type, ContainerElement.class))) {
								throw new OperationNotAllowedException(LogMessage.getMessage("green-0028", field.getName(), currentClass.getSimpleName()));
						}

						fieldList.add(field);
					}
				}

				if(++i == classes.length)
					break;

				currentClass = classes[i];
			} while(true);

			fields = fieldList.toArray(new Field[fieldList.size()]);

			GenericReflection.registerDeclaredFields(currentClass, "container:elements", fields);
		}

		return fields;
	}
	
	private static boolean isAcceptableElement(Class<?> type) {
		return
			type.equals(TextareaElement.class) ||
			type.equals(InputTextElement.class) ||
			type.equals(InputPasswordElement.class) ||
			type.equals(InputHiddenElement.class) ||
			type.equals(InputRadioElement.class) ||
			type.equals(InputFileElement.class) ||
			type.equals(SelectElement.class) ||
			type.equals(SelectMultipleElement.class);
	}

	public static HashMap<Integer, ContainerElement<?>> getContainers(Form form) {
		if(form.containers == null)
			form.containers = new HashMap<Integer, ContainerElement<?>>();

		return form.containers;
	}
	
	public static void fill(Element e, Field[] elementFields) {
		Gson g = new Gson();
		JsonArray fields = new JsonArray();
		for (Field field : elementFields) {
			Object value = GenericReflection.NoThrow.getValue(field, e);
			if(value != null) {
				final Class<?> fieldType = field.getType();
				if(isAcceptableElement(fieldType))
					continue;
				
				final boolean isArray;
				if(isArray = fieldType.isArray() && ClassUtils.isParent(fieldType.getComponentType(), ContainerElement.class) || ClassUtils.isParent(fieldType, ContainerElement.class)) {
					if(isArray) {
						for(ContainerElement<?> container: (ContainerElement<?>[])value)
							container.fill();
					}else
						((ContainerElement<?>) value).fill();
				} else {
					ElementValue annotation = field.getAnnotation(ElementValue.class);
					String name = annotation.name();
					if(name.isEmpty())
						name = field.getName();
					
					JsonObject o = new JsonObject();
					o.addProperty("name", name);
					o.add("values", g.toJsonTree(value));
					fields.add(o);
				}
			}
		}
		
		if(fields.size() > 0)
			DOMHandle.CustomMethod.call(e, "fillForm", fields);
	}
}
