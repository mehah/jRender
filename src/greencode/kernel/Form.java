package greencode.kernel;

import greencode.http.enumeration.RequestMethod;
import greencode.jscript.Window;
import greencode.jscript.elements.custom.ContainerElement;
import greencode.jscript.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.form.annotation.ConvertDateTime;
import greencode.util.ArrayUtils;
import greencode.util.ClassUtils;
import greencode.util.DateUtils;
import greencode.util.GenericReflection;
import greencode.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

final class Form {
	static void processRequestedForm(GreenContext context) throws IllegalArgumentException, IllegalAccessException, IllegalStateException, IOException, ServletException {
		String formName = context.request.getParameter("__requestedForm");
		if (formName != null) {
			final Class<? extends greencode.jscript.Form> formClass = Cache.forms.get(formName);
			if (formClass == null)
				Console.error(LogMessage.getMessage("green-0032", formName));

			context.requestedForm = context.currentWindow.document.forms(formClass);

			final boolean METHOD_TYPE_IS_GET = context.getRequest().isMethod(RequestMethod.GET);
			
			processElements(context, context.requestedForm, METHOD_TYPE_IS_GET, null, null);
		}
	}
	
	private static void processElements(final GreenContext context, final ContainerElementImplementation container, final boolean METHOD_TYPE_IS_GET, Map<String, Object> map, HashMap<Integer, ContainerElement<?>> containersForm) throws IllegalArgumentException, IllegalStateException, IllegalAccessException, IOException, ServletException {
		Field[] fields = greencode.jscript.$Container.getElementFields(container);
		for (Field f : fields) {
			greencode.jscript.form.annotation.ElementValue element = f.getAnnotation(greencode.jscript.form.annotation.ElementValue.class);

			final String parametro = !element.name().isEmpty() ? element.name() : f.getName();
			
			final Class<?> fieldType = f.getType(); 
			
			Object valor;

			if(fieldType.isArray() && ClassUtils.isParent(fieldType.getComponentType(), ContainerElement.class) || ClassUtils.isParent(fieldType, ContainerElement.class)) {
				Object json = map == null ? context.request.getParameter(parametro) : map.get(parametro);
				if(json == null)
					continue;
				
				final List<HashMap<String, Object>> list = json instanceof String ? context.gsonInstance.fromJson(context.request.getParameter(parametro), (new ArrayList<HashMap<String, Object>>()).getClass()) : (List<HashMap<String, Object>>) json;
								
				if(containersForm == null) {
					containersForm = greencode.jscript.$Container.getContainers(context.requestedForm);
					containersForm.clear();
				}
				
				if(fieldType.isArray()) {
					Class<? extends ContainerElement<?>> clazz = (Class<? extends ContainerElement<?>>) fieldType.getComponentType();
					ContainerElement<?>[] containers = (ContainerElement[]) Array.newInstance(clazz, list.size());
					for (int i = -1; ++i < containers.length;) {
						ContainerElement<?> containerElement = (ContainerElement<?>) GenericReflection.NoThrow.newInstance(clazz, new Class<?>[]{Window.class}, context.currentWindow);
						containers[i] = containerElement;
						
						Map<String, Object> containerMap = list.get(i);
						
						Integer uid = Integer.parseInt((String)containerMap.get("__uid"));
						greencode.jscript.$DOMHandle.setUID(containerElement, uid);					
						
						containersForm.put(uid, containerElement);
						
						processElements(context, containerElement, METHOD_TYPE_IS_GET, containerMap, containersForm);
					}
					valor = containers;
				} else {
					ContainerElement<?> containerElement = (ContainerElement<?>) GenericReflection.NoThrow.newInstance(fieldType, new Class<?>[]{Window.class}, context.currentWindow);
					valor = containerElement;
					
					Map<String, Object> containerMap = list.get(0);
					
					Integer uid = Integer.parseInt((String)containerMap.get("__uid"));
					
					greencode.jscript.$DOMHandle.setUID(containerElement, uid);					
					
					containersForm.put(uid, containerElement);
					
					processElements(context, containerElement, METHOD_TYPE_IS_GET, containerMap, containersForm);
				}
				f.set(container, valor);
			}else if (fieldType.equals(Part.class)) {
				f.set(container, GreenContext.getInstance().getRequest().getPart(parametro));
			} else if (fieldType.isArray()) {
				final String[] valores;
				if(map == null) {
					valores = context.request.getParameterValues(parametro + "[]");
				} else {
					List<Object> list = (List<Object>) map.get(parametro);
					valores = list == null ? null : list.toArray(new String[list.size()]);
				}
				
				if (valores != null) {
					final Object[] values = (Object[]) Array.newInstance(ClassUtils.toWrapperClass(fieldType.getComponentType()), valores.length);

					try {
						for (int i = -1; ++i < valores.length;) {
							Object _value = greencode.kernel.Form.getFieldValue(f, fieldType.getComponentType(), valores[i]);

							if (_value == null)
								Console.error(LogMessage.getMessage("green-0019", f.getName(), f.getDeclaringClass().getSimpleName()));

							if (_value instanceof String) {
								if (element.trim())
									_value = ((String) _value).trim();

								if (METHOD_TYPE_IS_GET)
									_value = StringUtils.toCharset((String) _value, GreenCodeConfig.View.charset);
							}

							values[i] = _value;
						}

						f.set(container, ClassUtils.isPrimitiveType(fieldType.getComponentType()) ? ArrayUtils.wrapperToPrimitive(values) : values);						
					} catch (UnknownFormatConversionException e) {
						Console.error(LogMessage.getMessage("green-0013", f.getName(), container.getClass().getSimpleName(), "Date", ConvertDateTime.class.getSimpleName()));
					}
				}else
					f.set(container, null);
			} else {
				try {
					Object result = (map == null ? context.request.getParameter(parametro) : map.get(parametro));
					if(result == null)
						valor = ClassUtils.isPrimitiveType(fieldType) ? ClassUtils.getDefaultValue(fieldType) : null;
					else {
						valor = greencode.kernel.Form.getFieldValue(f, fieldType, result.toString());

						if (valor instanceof String) {
							if (element.trim())
								valor = ((String) valor).trim();

							if (METHOD_TYPE_IS_GET)
								valor = StringUtils.toCharset((String) valor, GreenCodeConfig.View.charset);
						}
					}					

					f.set(container, valor);
				} catch (UnknownFormatConversionException e) {
					Console.error(LogMessage.getMessage("green-0013", f.getName(), container.getClass().getSimpleName(), "Date", ConvertDateTime.class.getSimpleName()));
				}
			}
		}
	}

	static Object getFieldValue(Field field, Class<?> instanceClass, final String valor) throws UnknownFormatConversionException {
		if (valor != null && !valor.isEmpty()) {
			instanceClass = ClassUtils.toWrapperClass(instanceClass);
			if (String.class.equals(instanceClass))
				return valor;
			if (Integer.class.equals(instanceClass))
				return Integer.parseInt(valor);
			if (Long.class.equals(instanceClass))
				return Long.parseLong(valor);
			if (Double.class.equals(instanceClass))
				return Double.parseDouble(valor);
			if (Float.class.equals(instanceClass))
				return Float.parseFloat(valor);
			if (Boolean.class.equals(instanceClass))
				return valor.equals("true") || valor.equals("1");
			if (Character.class.equals(instanceClass))
				return valor.charAt(0);
			if (Byte.class.equals(instanceClass))
				return Byte.parseByte(valor);
			if (Short.class.equals(instanceClass))
				return Short.parseShort(valor);

			if (Date.class.equals(instanceClass)) {
				if (!field.isAnnotationPresent(ConvertDateTime.class))
					throw new UnknownFormatConversionException("green-0013");

				ConvertDateTime convert = field.getAnnotation(ConvertDateTime.class);

				try {
					return DateUtils.toDate(valor, convert.pattern());
				} catch (ParseException e) {
					Console.warning(LogMessage.getMessage("green-0033", valor, convert.pattern()));
				}
			} else {
				/* return HttpParameter.Context.getObjectRequest(valor); */
			}
		}

		return null;
	}
}
