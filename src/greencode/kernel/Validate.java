package greencode.kernel;

import greencode.jscript.Form;
import greencode.jscript.elements.custom.ContainerElement;
import greencode.jscript.elements.custom.implementation.ContainerElementImplementation;
import greencode.jscript.form.annotation.Validator;
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

		final Field[] fields = greencode.jscript.$Container.getElementFields(__container);
		if(fields == null)
			return true;

		for(Field f: fields) {
			greencode.jscript.form.annotation.ElementValue element = f.getAnnotation(greencode.jscript.form.annotation.ElementValue.class);

			final greencode.jscript.window.annotation.Validate methodValidate = requestMethod.getAnnotation(greencode.jscript.window.annotation.Validate.class);

			if(methodValidate.blocks().length > 0 && !ArrayUtils.contains(methodValidate.blocks(), element.blockName()))
				continue;

			final String parametro = !element.name().isEmpty() ? element.name() : f.getName();

			if(methodValidate.fields().length > 0 && !ArrayUtils.contains(methodValidate.fields(), parametro))
				continue;

			final boolean validateIsPartial = methodValidate.type().equals(ValidateType.PARTIAL);

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
				final Object valor = GenericReflection.NoThrow.getValue(f, __container);
				for(Validator validator: element.validators()) {
					if(!validate(context, form, container, parametro, validator, valor, dataValidation)) {
						if(validateIsPartial)
							return false;

						break;
					}
				}
			}
		}

		return true;
	}

	private static boolean validate(GreenContext context, Form form, ContainerElement<?> container, String name, Validator validation, Object value, DataValidation dataValidation) {
		final greencode.validator.Validator oValidation = ValidatorFactory.getValidationInstance(context.getRequest().getViewSession(), validation.value());

		Console.log("Calling Validator of "+name+": [" + oValidation.getClass().getSimpleName() + "]");

		boolean res = oValidation.validate(context.currentWindow, form, container, name, value, validation.labels(), dataValidation);
		if(!res)
			context.executeAction = false;

		return res;
	}
}
