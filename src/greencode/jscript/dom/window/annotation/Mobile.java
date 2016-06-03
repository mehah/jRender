package greencode.jscript.dom.window.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mobile {
	public String path();
	public String selector() default "";
	public String ajaxSelector() default "";
	public PageParameter[] parameters() default @PageParameter(name = "", value = "");
}
