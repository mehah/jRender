package com.jrender.jscript.dom.form.convert;

import java.lang.reflect.Type;

import com.jrender.kernel.JRenderContext;

public interface Converter {
	public Object set(JRenderContext context, Type fieldType, Object value);
}
