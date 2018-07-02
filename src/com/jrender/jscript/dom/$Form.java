package com.jrender.jscript.dom;

import com.jrender.validator.DataValidation;

public class $Form {
	private $Form() {}
	
	public static void setDataValidation(Form form, DataValidation dataValidation) {
		form.dataValidation = dataValidation;
	}
	
}
