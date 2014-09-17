package greencode.jscript.window.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Keep {
	public int poolingTime() default 1000;
	public boolean autoFlush() default true;
	public String onConnectionLost() default "";
}
