package com.jrender.jscript.dom.form.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validator {
	public Class<? extends com.jrender.validator.Validator> value();
	public String[] labels() default "";
}
