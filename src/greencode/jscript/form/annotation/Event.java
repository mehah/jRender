package greencode.jscript.form.annotation;

import greencode.http.enumeration.RequestMethod;
import greencode.jscript.Window;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
	public String[] name();
	public Class<? extends Window> windowAction();
	public String method() default "init";
	public RequestMethod requestMethod() default RequestMethod.GET;
}
