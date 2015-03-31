package greencode.validator;

import greencode.http.ViewSession;

import java.util.HashMap;

public abstract class ValidatorFactory {
	private static HashMap<Class<? extends Validator>, Validator> getValidators(final ViewSession viewSession) {
		@SuppressWarnings("unchecked")
		HashMap<Class<? extends Validator>, Validator> validators = (HashMap<Class<? extends Validator>, Validator>) viewSession.getAttribute("__VALIDATORS__");
		
		if(validators == null)
			viewSession.setAttribute("__VALIDATORS__", validators = new HashMap<Class<? extends Validator>, Validator>());
	
		return validators;
	}
	
	public static<V extends Validator> V getValidationInstance(final ViewSession viewSession, final Class<V> v)
	{
		final HashMap<Class<? extends Validator>, Validator> validators = getValidators(viewSession);
		
		@SuppressWarnings("unchecked")
		V validation = (V) validators.get(v);
		
		if(validation == null) {
			try {
				validators.put(v, validation = v.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return validation;
	}
}
