package greencode.jscript;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import greencode.kernel.LogMessage;
import greencode.util.GenericReflection;

public final class ElementHandle {
	public static Element getInstance(Window window) { return new Element(window); }	
	
	public static void dataTransfer(Element of, Element to) {		
		greencode.jscript.$DOMHandle.setUID(to, DOMHandle.getUID(of));
		greencode.jscript.$DOMHandle.setVariables(to, greencode.jscript.$DOMHandle.getVariables(of));
		
		String type = DOMHandle.containVariableKey(to, "type") ? DOMHandle.getVariableValue(to, "type", String.class) : null;
		if(type != null)
			DOMHandle.setVariableValue(to, "type", type);
	}
	
	public static<E extends Element> E cast(Element element, Class<E> castTo) {
		try {
			if(Modifier.isAbstract(castTo.getModifiers()))
				throw new RuntimeException(LogMessage.getMessage("green-0037", castTo.getSimpleName()));
			
			E e = GenericReflection.NoThrow.getDeclaredConstrutor(castTo, Window.class).newInstance(element.window);
		
			dataTransfer(element, e);
			
			return e;
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}		
	}
	
	public static<E extends Element> E[] cast(Element[] elements, Class<E> castTo) {
		@SuppressWarnings("unchecked")
		E[] list = (E[]) Array.newInstance(castTo, elements.length);
		
		for (int i = -1; ++i < elements.length;)
			list[i] = cast(elements[i], castTo);
		
		return list;
	}
	
	public static void empty(Element e) {
		DOMHandle.execCommand(e, "@customMethod.empty");
	}
	
	public static Element getOrCreateElementByTagName(Element owner, String tagName) {
		Element e = new Element(owner.window);		
		DOMHandle.registerElementByCommand(owner, e, "@customMethod.getOrCreateElementByTagName", tagName);		
		return e;
	}
}
