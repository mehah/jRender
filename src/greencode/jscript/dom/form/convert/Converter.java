package greencode.jscript.dom.form.convert;

import java.lang.reflect.Type;

import greencode.kernel.GreenContext;

public interface Converter {
	public Object set(GreenContext context, Type fieldType, Object value);
}
