package com.jrender.validator;

public final class $DataValidation {
	public static void putError(DataValidation data, Class<? extends Validator> classError) {
		data.errors.add(classError);
	}
}
