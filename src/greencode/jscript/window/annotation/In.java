package greencode.jscript.window.annotation;

import greencode.http.Conversation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface In {
	public boolean create() default false;
	public int conversationId() default Conversation.CURRENT;
}
