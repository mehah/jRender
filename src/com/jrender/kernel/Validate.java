package com.jrender.kernel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.elements.InputElement;
import com.jrender.jscript.dom.elements.SelectElement;
import com.jrender.jscript.dom.elements.SelectMultipleElement;
import com.jrender.jscript.dom.elements.TextareaElement;
import com.jrender.jscript.dom.elements.custom.ContainerElement;
import com.jrender.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import com.jrender.jscript.dom.form.annotation.Validator;
import com.jrender.util.ArrayUtils;
import com.jrender.util.ClassUtils;
import com.jrender.util.GenericReflection;
import com.jrender.validator.DataValidation;
import com.jrender.validator.ValidateType;
import com.jrender.validator.ValidatorFactory;

final class Validate {
	private Validate() {}

	static boolean validate(JRenderContext context, Method requestMethod, Form form, ContainerElement<?> container, DataValidation dataValidation) {
		final ContainerElementImplementation __container = container == null ? form : container;

		final Field[] fields = com.jrender.jscript.dom.$Container.getElementFields(__container);
		if(fields == null)
			return true;

		for(Field f: fields) {
			com.jrender.jscript.dom.form.annotation.ElementValue element = f.getAnnotation(com.jrender.jscript.dom.form.annotation.ElementValue.class);

			final com.jrender.jscript.dom.window.annotation.Validate methodValidate = requestMethod.getAnnotation(com.jrender.jscript.dom.window.annotation.Validate.class);

			if(methodValidate.blocks().length > 0 && !ArrayUtils.contains(methodValidate.blocks(), element.blockName()))
				continue;

			final String parametro = !element.name().isEmpty() ? element.name() : f.getName();

			if(methodValidate.fields().length > 0 && !ArrayUtils.contains(methodValidate.fields(), parametro))
				continue;

			if(f.getType().isArray() && ClassUtils.isParent(f.getType().getComponentType(), ContainerElement.class)) {
				ContainerElement<?>[] v = (ContainerElement[]) GenericReflection.NoThrow.getValue(f, __container);
				for(ContainerElement<?> containerElement: v) {
					if(!validate(context, requestMethod, form, containerElement, dataValidation))
						return false;
				}

				continue;
			} else if(ClassUtils.isParent(f.getType(), ContainerElement.class)) {
				if(!validate(context, requestMethod, form, (ContainerElement<?>) GenericReflection.NoThrow.getValue(f, __container), dataValidation))
					return false;

				continue;
			}

			if(element.validators().length > 0) {
				final boolean validateIsPartial = methodValidate.type().equals(ValidateType.PARTIAL);
				
				final Element elementObject;
				Object valor = GenericReflection.NoThrow.getValue(f, __container);
				if(valor instanceof Element) {
					elementObject = (Element) valor;
					if(valor instanceof InputElement || valor instanceof TextareaElement || valor instanceof SelectElement || valor instanceof SelectMultipleElement) {
						valor = DOMHandle.getVariableValue((Element)valor, "value", Object.class);
					}
				}else
					elementObject = null;

				for(Validator validator: element.validators()) {
					if(!validate(context, form, container, elementObject, parametro, valor, validator, dataValidation)) {
						if(validateIsPartial)
							return false;

						break;
					}
				}
			}
		}

		return true;
	}

	private static boolean validate(JRenderContext context, Form form, ContainerElement<?> container, Element element, String name, Object value, Validator validation, DataValidation dataValidation) {
		final com.jrender.validator.Validator oValidation = ValidatorFactory.getValidationInstance(context.getRequest().getViewSession(), validation.value());

		Console.log("Calling Validator of "+name+": [" + oValidation.getClass().getSimpleName() + "]");

		boolean res = oValidation.validate(context.currentWindow, form, container, element, name, value, validation.labels(), dataValidation);
		if(!res) {
			com.jrender.validator.$DataValidation.putError(dataValidation, validation.value());
		}

		return res;
	}
}
