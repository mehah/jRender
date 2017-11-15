package com.jrender.jscript.dom.window.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jrender.http.Conversation;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface In {
	public boolean create() default false;
	public int conversationId() default Conversation.CURRENT;
}
