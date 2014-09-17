package greencode.kernel;

import greencode.http.HttpRequest;
import greencode.jscript.Form;
import greencode.jscript.form.ValidatorFactory;
import greencode.jscript.form.annotation.Validator;
import greencode.jscript.window.enumeration.ValidateType;
import greencode.util.ArrayUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class Validate {
	private Validate() {}
	
	static void validate(GreenContext context, Method requestMethod, Form form) {
		final Field[] fields = greencode.jscript.$Form.processFields(form.getClass());

		final HttpRequest request = context.getRequest();
		
		if(fields != null) {
			boolean stoppedProcess = false;
			for (Field f : fields) {
				greencode.jscript.form.annotation.ElementValue element = f.getAnnotation(greencode.jscript.form.annotation.ElementValue.class);
				
				final String parametro = !element.name().isEmpty() ? element.name() : f.getName();
				
				final Object valor = f.getType().isArray() ? request.getParameterValues(parametro+"[]") : request.getParameter(parametro);
								
				final greencode.jscript.window.annotation.Validate methodValidate = requestMethod.getAnnotation(greencode.jscript.window.annotation.Validate.class);
								
				final boolean validateIsPartial = methodValidate.type().equals(ValidateType.PARTIAL);
				
				if(methodValidate.blocks().length > 0 && !ArrayUtils.contains(methodValidate.blocks(), element.blockName()))
					continue;
				
				if(methodValidate.fields().length > 0 && !ArrayUtils.contains(methodValidate.fields(), parametro))
					continue;
				
				if(element.validators().length > 0) {					
					for (Validator validator : element.validators()) {
						if(!validate(context, form, parametro, validator, valor)) {
							if(validateIsPartial)
								return;
							
							stoppedProcess = true;									
							break;
						}
					}
				}
			}
			
			if(stoppedProcess)
				return;
		}
	}
		
	private static boolean validate(GreenContext context, Form form, String name, Validator validation, Object value) {
		boolean executarAction = true;
		
		final greencode.jscript.form.Validator oValidation = ValidatorFactory.getValidationInstance(context.getRequest().getViewSession(), validation.value());
							
		Console.log("Calling Validator: ["+oValidation.getClass().getSimpleName()+"]");
		
		try {	
			executarAction = oValidation.validate(context.currentWindow, form, name, value, validation.labels());
			
			if(!executarAction)
				greencode.kernel.$GreenContext.executeAction(context, false);			
		} catch (Exception e) {
			Console.error(e);
		}
		
		return executarAction;
	}
}
