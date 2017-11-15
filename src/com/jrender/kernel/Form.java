package com.jrender.kernel;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import com.jrender.exception.JRenderError;
import com.jrender.http.enumeration.RequestMethod;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.Window;
import com.jrender.jscript.dom.WindowHandle;
import com.jrender.jscript.dom.elements.InputFileElement;
import com.jrender.jscript.dom.elements.custom.ContainerElement;
import com.jrender.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import com.jrender.jscript.dom.form.annotation.ConvertDateTime;
import com.jrender.jscript.dom.form.convert.Converter;
import com.jrender.util.ArrayUtils;
import com.jrender.util.ClassUtils;
import com.jrender.util.DateUtils;
import com.jrender.util.GenericReflection;
import com.jrender.util.LogMessage;
import com.jrender.util.StringUtils;

final class Form {
	static void processRequestedForm(JRenderContext context) throws IllegalArgumentException, IllegalAccessException, IllegalStateException, IOException, ServletException {
		String formName = context.request.getParameter("__requestedForm");
		if(formName != null) {
			if(formName.equals("null"))
				throw new JRenderError(LogMessage.getMessage("0049"));
				
			final Class<? extends com.jrender.jscript.dom.Form> formClass = Cache.forms.get(formName);
			if(formClass == null)
				throw new JRenderError(LogMessage.getMessage("0032", formName));

			context.requestedForm = context.currentWindow.document.forms(formClass);

			final boolean METHOD_TYPE_IS_GET = context.getRequest().isMethod(RequestMethod.GET);

			processElements(context, context.requestedForm, METHOD_TYPE_IS_GET, null, null);
		}
	}

	private static void processElements(final JRenderContext context, final ContainerElementImplementation container, final boolean METHOD_TYPE_IS_GET, Map<String, Object> map, Map<Integer, ContainerElement<?>> containersForm) throws IllegalArgumentException, IllegalStateException, IllegalAccessException, IOException, ServletException {
		Field[] fields = com.jrender.jscript.dom.$Container.getElementFields(container);
		for(Field f: fields) {
			com.jrender.jscript.dom.form.annotation.ElementValue element = f.getAnnotation(com.jrender.jscript.dom.form.annotation.ElementValue.class);

			final String parametro = !element.name().isEmpty() ? element.name() : f.getName();

			final Class<?> fieldType = f.getType();

			Object valor;

			if(fieldType.isArray() && ClassUtils.isParent(fieldType.getComponentType(), ContainerElement.class) || ClassUtils.isParent(fieldType, ContainerElement.class)) {
				Object json = map == null ? context.request.getParameter(parametro) : map.get(parametro);
				if(json == null)
					continue;

				final List<Map<String, Object>> list = json instanceof String ? context.gsonInstance.fromJson(context.request.getParameter(parametro), (new ArrayList<Map<String, Object>>()).getClass()) : (List<Map<String, Object>>) json;

				if(containersForm == null) {
					containersForm = com.jrender.jscript.dom.$Container.getContainers(context.requestedForm);
					containersForm.clear();
				}

				if(fieldType.isArray()) {
					Class<? extends ContainerElement<?>> clazz = (Class<? extends ContainerElement<?>>) fieldType.getComponentType();
					ContainerElement<?>[] containers = (ContainerElement[]) Array.newInstance(clazz, list.size());
					for(int i = -1; ++i < containers.length;) {
						ContainerElement<?> containerElement = (ContainerElement<?>) GenericReflection.NoThrow.newInstance(clazz, new Class<?>[]{Window.class}, context.currentWindow);
						containers[i] = containerElement;

						Map<String, Object> containerMap = list.get(i);

						Integer uid = Integer.parseInt((String) containerMap.get("__uid"));
						com.jrender.jscript.$DOMHandle.setUID(containerElement, uid);

						containersForm.put(uid, containerElement);

						processElements(context, containerElement, METHOD_TYPE_IS_GET, containerMap, containersForm);
					}
					valor = containers;
				} else {
					ContainerElement<?> containerElement = (ContainerElement<?>) GenericReflection.NoThrow.newInstance(fieldType, new Class<?>[]{Window.class}, context.currentWindow);
					valor = containerElement;

					Map<String, Object> containerMap = list.get(0);

					Integer uid = Integer.parseInt((String) containerMap.get("__uid"));

					com.jrender.jscript.$DOMHandle.setUID(containerElement, uid);

					containersForm.put(uid, containerElement);

					processElements(context, containerElement, METHOD_TYPE_IS_GET, containerMap, containersForm);
				}
				f.set(container, valor);
			} else if(fieldType.equals(Part.class)) {
				f.set(container, context.getRequest().getPart(parametro));
			} else if(fieldType.equals(InputFileElement.class)) {
				DOMHandle.setVariableValue((Element) f.get(container), "value", context.getRequest().getPart(parametro));
			} else {
				final Type type = f.getGenericType();
				final boolean isElementMultipleValue = com.jrender.jscript.dom.elements.$Element.isValueMultiSelectable(fieldType);

				if(isElementMultipleValue || fieldType.isArray()) {
					final String[] valores;
					if(map == null) {
						String[] _valores = context.request.getParameterValues(parametro + "[]");
						if(_valores == null) {
							valor = context.request.getParameter(parametro);
							valores = valor != null ? new String[]{(String) valor} : null;
						} else
							valores = _valores;
					} else {
						List<Object> list = (List<Object>) map.get(parametro);
						valores = list == null ? null : list.toArray(new String[list.size()]);
					}

					if(valores == null) {
						if(isElementMultipleValue)
							DOMHandle.setVariableValue((Element) f.get(container), "value", null);
						else
							f.set(container, null);
					} else {
						final Class<?> realFieldType = type instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0] : fieldType.getComponentType();

						final Object[] values = (Object[]) Array.newInstance(ClassUtils.toWrapperClass(realFieldType), valores.length);

						try {
							for(int i = -1; ++i < valores.length;) {
								Object _value = com.jrender.kernel.Form.getFieldValue(context, f, realFieldType, valores[i], element);

								if(_value == null)
									throw new JRenderError(LogMessage.getMessage("0019", f.getName(), f.getDeclaringClass().getSimpleName()));

								if(_value instanceof String) {
									if(element.trim())
										_value = ((String) _value).trim();
									
									if(element.removeMultipleSpaces())
										_value = StringUtils.removeMultipleSpaces(((String) _value));

									if(METHOD_TYPE_IS_GET)
										_value = StringUtils.toCharset((String) _value, JRenderConfig.Server.View.charset);
								}

								values[i] = _value;
							}

							if(isElementMultipleValue) {
								DOMHandle.setVariableValue((Element) f.get(container), "value", values);
							} else {
								f.set(container, ClassUtils.isPrimitiveType(fieldType.getComponentType()) ? ArrayUtils.wrapperToPrimitive(values) : values);
							}
						} catch(UnknownFormatConversionException e) {
							throw new JRenderError(LogMessage.getMessage("0013", f.getName(), container.getClass().getSimpleName(), "Date", ConvertDateTime.class.getSimpleName()));
						}
					}
				} else {
					try {
						Object result = (map == null ? context.request.getParameter(parametro) : map.get(parametro));

						final Class<?> realFieldType = type instanceof ParameterizedType ? (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0] : f.getType();

						if(result == null) {
							valor = ClassUtils.isPrimitiveType(realFieldType) ? ClassUtils.getDefaultValue(realFieldType) : null;
						} else {
							valor = com.jrender.kernel.Form.getFieldValue(context, f, realFieldType, result.toString(), element);

							if(valor instanceof String) {
								if(element.trim())
									valor = ((String) valor).trim();
								
								if(element.removeMultipleSpaces())
									valor = StringUtils.removeMultipleSpaces(((String) valor));

								if(((String) valor).isEmpty()) {
									valor = null;
								} else if(METHOD_TYPE_IS_GET) {
									valor = StringUtils.toCharset((String) valor, JRenderConfig.Server.View.charset);
								}
							}
						}

						if(com.jrender.jscript.dom.elements.$Element.isElementWithValue(fieldType)) {
							DOMHandle.setVariableValue((Element) f.get(container), "value", valor);
						} else {
							f.set(container, fieldType.isEnum() ? GenericReflection.getEnumValue(fieldType, (String) valor) : valor);
						}
					} catch(UnknownFormatConversionException e) {
						throw new JRenderError(LogMessage.getMessage("0013", f.getName(), container.getClass().getSimpleName(), "Date", ConvertDateTime.class.getSimpleName()));
					}
				}
			}
		}
	}

	static Object getFieldValue(JRenderContext context, Field field, Class<?> instanceClass, final String valor, final com.jrender.jscript.dom.form.annotation.ElementValue elementAnnotation) throws UnknownFormatConversionException {
		if(valor != null && !valor.isEmpty()) {
			if(elementAnnotation.converters().length > 0) {
				Object lastValue = valor;
				for(Class<? extends Converter> clazz: elementAnnotation.converters()) {
					Converter c = GenericReflection.NoThrow.newInstance(clazz, new Class<?>[0]);
					lastValue = c.set(context, instanceClass, lastValue);
				}

				return lastValue;
			}

			instanceClass = ClassUtils.toWrapperClass(instanceClass);
			if(String.class.equals(instanceClass))
				return valor;
			if(Integer.class.equals(instanceClass))
				return Integer.parseInt(valor);
			if(Long.class.equals(instanceClass))
				return Long.parseLong(valor);
			if(Double.class.equals(instanceClass))
				return Double.parseDouble(valor);
			if(Float.class.equals(instanceClass))
				return Float.parseFloat(valor);
			if(Boolean.class.equals(instanceClass))
				return valor.equals("true") || valor.equals("1");
			if(Character.class.equals(instanceClass))
				return valor.charAt(0);
			if(Byte.class.equals(instanceClass))
				return Byte.parseByte(valor);
			if(Short.class.equals(instanceClass))
				return Short.parseShort(valor);

			if(Date.class.equals(instanceClass)) {
				if(!field.isAnnotationPresent(ConvertDateTime.class))
					throw new UnknownFormatConversionException("0013");

				ConvertDateTime convert = field.getAnnotation(ConvertDateTime.class);

				try {
					return DateUtils.toDate(valor, convert.pattern());
				} catch(ParseException e) {
					throw new JRenderError(LogMessage.getMessage("0033", valor, convert.pattern()));
				}
			}
			
			Object v = WindowHandle.getObjectParamter(context.currentWindow, (String) valor);			
			return v == null ? valor : v;
		}

		return null;
	}
}
