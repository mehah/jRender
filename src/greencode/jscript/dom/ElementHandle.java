package greencode.jscript.dom;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import greencode.exception.GreencodeError;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.elements.SelectElementPrototype;
import greencode.util.ClassUtils;
import greencode.util.GenericReflection;
import greencode.util.LogMessage;

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
		greencode.jscript.$DOMHandle.setUID(to, DOMHandle.getUID(of));
		greencode.jscript.$DOMHandle.setVariables(to, greencode.jscript.$DOMHandle.getVariables(of));

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
				throw new GreencodeError(LogMessage.getMessage("green-0037", castTo.getSimpleName()));

			E e;

			if (typeValue == null) {
				if (castTo.getTypeParameters().length > 0)
					typeValue = String.class;
			} else if (castTo.getTypeParameters().length == 0)
				throw new GreencodeError(LogMessage.getMessage("green-0048"));
			else {
				if (!ClassUtils.isWrapperType(typeValue))
					throw new GreencodeError(LogMessage.getMessage("green-0047"));
			}

			e = typeValue == null ? ElementHandle.getInstance(castTo, greencode.jscript.$DOMHandle.getWindow(element)) : ElementHandle.getInstance(castTo, greencode.jscript.$DOMHandle.getWindow(element), typeValue);

			dataTransfer(element, e);

			return e;
		} catch (Exception e1) {
			throw new GreencodeError(e1);
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
