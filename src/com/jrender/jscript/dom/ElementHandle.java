package com.jrender.jscript.dom;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import com.jrender.exception.JRenderError;
import com.jrender.jscript.DOMHandle;
import com.jrender.util.ClassUtils;
import com.jrender.util.GenericReflection;
import com.jrender.util.LogMessage;

public final class ElementHandle {
	public static Element getInstance(Window window) {
		return new Element(window);
	}

	public static <E extends Element> E getInstance(Class<E> clazz, Window window) {
		return GenericReflection.NoThrow.newInstance(clazz, new Class<?>[] { Window.class }, window);
	}

	public static <E extends Element> E getInstance(Class<E> clazz, Window window, Class<?> typeValue) {
		return GenericReflection.NoThrow.newInstance(clazz, new Class<?>[] { Window.class, Class.class }, window, typeValue);
	}

	public static void dataTransfer(Element of, Element to) {
		com.jrender.jscript.$DOMHandle.setUID(to, DOMHandle.getUID(of));
		com.jrender.jscript.$DOMHandle.setVariables(to, com.jrender.jscript.$DOMHandle.getVariables(of));

		String type = DOMHandle.containVariableKey(to, "type") ? DOMHandle.getVariableValue(to, "type", String.class) : null;
		if (type != null)
			DOMHandle.setVariableValue(to, "type", type);
	}

	public static <E extends Element> E cast(Element element, Class<E> castTo) {
		return cast(element, castTo, null);
	}

	public static <E extends Element> E cast(Element element, Class<E> castTo, Class<?> typeValue) {
		try {
			if (castTo.equals(Element.class))
				return (E) element;

			if (Modifier.isAbstract(castTo.getModifiers()))
				throw new JRenderError(LogMessage.getMessage("0037", castTo.getSimpleName()));

			E e;

			if (typeValue == null) {
				if (castTo.getTypeParameters().length > 0)
					typeValue = String.class;
			} else if (castTo.getTypeParameters().length == 0)
				throw new JRenderError(LogMessage.getMessage("0048"));
			else {
				if (!ClassUtils.isWrapperType(typeValue))
					throw new JRenderError(LogMessage.getMessage("0047"));
			}

			e = typeValue == null ? ElementHandle.getInstance(castTo, com.jrender.jscript.$DOMHandle.getWindow(element)) : ElementHandle.getInstance(castTo, com.jrender.jscript.$DOMHandle.getWindow(element), typeValue);

			dataTransfer(element, e);

			return e;
		} catch (Exception e1) {
			throw new JRenderError(e1);
		}
	}

	public static <E extends Element> E[] cast(Element[] elements, Class<E> castTo) {
		if (castTo.equals(Element.class))
			return (E[]) elements;

		E[] list = (E[]) Array.newInstance(castTo, elements.length);

		for (int i = -1; ++i < elements.length;)
			list[i] = cast(elements[i], castTo);

		return list;
	}
}
