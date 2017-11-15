package com.jrender.jscript.dom.window.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jrender.validator.ValidateType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
	public String[] fields() default {};
	public String[] blocks() default {};
	public ValidateType type() default ValidateType.PARTIAL;
}
