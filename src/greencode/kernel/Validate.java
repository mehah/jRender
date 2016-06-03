package greencode.kernel;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.Form;
import greencode.jscript.dom.elements.InputElement;
import greencode.jscript.dom.elements.SelectElement;
import greencode.jscript.dom.elements.SelectMultipleElement;
import greencode.jscript.dom.elements.TextareaElement;
import greencode.jscript.dom.elements.custom.ContainerElement;
import greencode.jscript.dom.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.dom.form.annotation.Validator;
import greencode.util.ArrayUtils;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.validator.DataValidation;
import greencode.validator.ValidateType;
import greencode.validator.ValidatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class Validate {
	private Validate() {}

	static boolean validate(GreenContext context, Method requestMethod, Form form, ContainerElement<?> container, DataValidation dataValidation) {
		final ContainerElementImplementation __container = container == null ? form : container;

		final Field[] fields = greencode.jscript.dom.$Container.getElementFields(__container);
		if(fields == null)
			return true;

		for(Field f: fields) {
			greencode.jscript.dom.form.annotation.ElementValue element = f.getAnnotation(greencode.jscript.dom.form.annotation.ElementValue.class);

			final greencode.jscript.dom.window.annotation.Validate methodValidate = requestMethod.getAnnotation(greencode.jscript.dom.window.annotation.Validate.class);

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

	private static boolean validate(GreenContext context, Form form, ContainerElement<?> container, Element element, String name, Object value, Validator validation, DataValidation dataValidation) {
		final greencode.validator.Validator oValidation = ValidatorFactory.getValidationInstance(context.getRequest().getViewSession(), validation.value());

		Console.log("Calling Validator of "+name+": [" + oValidation.getClass().getSimpleName() + "]");

		boolean res = oValidation.validate(context.currentWindow, form, container, element, name, value, validation.labels(), dataValidation);
		if(!res)
			context.executeAction = false;

		return res;
	}
}
