package greencode.jscript;

import java.lang.reflect.Modifier;

import greencode.kernel.LogMessage;
import greencode.util.GenericReflection;

public final class ElementHandle {
	public static Element getInstance(Window window) { return new Element(window); }
	
	public static<E extends Element> E cast(Element element, Class<E> castTo) {
		try {
			if(Modifier.isAbstract(castTo.getModifiers()))
				throw new RuntimeException(LogMessage.getMessage("green-0037", castTo.getSimpleName()));
			
			E e = GenericReflection.NoThrow.getDeclaredConstrutor(castTo, Window.class).newInstance(element.window);
		
			String type = DOMHandle.containVariableKey(e, "type") ? DOMHandle.getVariableValue(e, "type", String.class) : null;
			
			greencode.jscript.$DOMHandle.setUID(e, DOMHandle.getUID(element));
			greencode.jscript.$DOMHandle.setVariables(e, greencode.jscript.$DOMHandle.getVariables(element));
			
			if(type != null)
				DOMHandle.setVariableValue(e, "type", type);
			
			return e;
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}		
	}
	
	public static void empty(Element e) {
		DOMHandle.execCommand(e, "customMethod.empty");
	}
}
