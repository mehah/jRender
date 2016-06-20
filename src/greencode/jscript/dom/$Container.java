package greencode.jscript.dom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.elements.custom.ContainerElement;
import greencode.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.dom.form.annotation.ElementValue;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;

public final class $Container {
	private $Container() {
	}

	public static Field[] getElementFields(ContainerElementImplementation e) {
		if(e instanceof Form)
			return ((Form) e).elementFields;
		else if(e instanceof ContainerElement)
			return greencode.jscript.dom.elements.custom.$Container.getElementFields((ContainerElement<?>) e);

		return null;
	}

	public static Field[] processFields(Class<? extends ContainerElementImplementation> currentClass) {
		Field[] fields = GenericReflection.getDeclaredFieldsByConditionId(currentClass, "container:elements");
		if(fields == null) {
			List<Field> fieldList = new ArrayList<Field>();

			Class<? extends ContainerElementImplementation>[] classes = (Class<? extends ContainerElementImplementation>[]) ClassUtils.getParents(currentClass, Form.class, ContainerElementImplementation.class);

			int i = -1;
			do {
				for(Field field: GenericReflection.getDeclaredFields(currentClass)) {
					if(field.isAnnotationPresent(greencode.jscript.dom.form.annotation.ElementValue.class)) {
						Class<?> type = field.getType();
						if(type.isArray())
							type = type.getComponentType();

						//TODO: Remover Futuramente para sistema de objeto em valor
						/*if(!(
							type.equals(Date.class) ||
							type.equals(Part.class) ||
							greencode.jscript.dom.elements.$Element.isElementWithValue(type) ||
							ClassUtils.isPrimitiveOrWrapper(type) ||
							ClassUtils.isParent(type, ContainerElement.class))) {
								throw new OperationNotAllowedException(LogMessage.getMessage("green-0028", field.getName(), currentClass.getSimpleName()));
						}*/

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

	public static Map<Integer, ContainerElement<?>> getContainers(Form form) {
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
				if(greencode.jscript.dom.elements.$Element.isElementWithValue(fieldType))
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
			DOMHandle.execCommand(e, "fillForm", fields);
	}
}
