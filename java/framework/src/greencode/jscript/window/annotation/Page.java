package greencode.jscript.window.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Page {
	public String name();
	public String path();
	//public String mobilePath();
	public String URLName() default "";
	public String selector() default "";
	public String ajaxSelector() default "";
}
