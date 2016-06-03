package greencode.jscript.dom.form.annotation;

import greencode.jscript.dom.form.convert.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementValue {
	public String name() default "";
	public String blockName() default "";
	public Validator[] validators() default {};
	public Class<? extends Converter>[] converters() default {};
	public boolean trim() default false;
}
