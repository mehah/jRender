package greencode.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Connection {
	public String value() default "";
	public String serverName() default "";
	public String database() default "";
	public String schema() default "";
	public String userName() default "";
	public String password() default "";
}
