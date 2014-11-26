package greencode.jscript;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.google.gson.Gson;

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
			if(castTo.equals(Element.class))
				return (E) element;
			
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
		if(castTo.equals(Element.class))
			return (E[]) elements;
		
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
	
	public static Element querySelector(Element owner, String selector, HashMap<String, String[]> attrs, boolean not) {
		Element e = new Element(owner.window);		
		DOMHandle.registerElementByCommand(owner, e, "@customMethod.querySelector", selector, attrs, not);		
		return e;
	}
	
	public static Element[] querySelectorAll(Element owner, String selector, HashMap<String, String[]> attrs, boolean not) {
		final int qnt = DOMHandle.getVariableValueByPropertyNoCache(owner, "querySelectorAll.length", Integer.class, "@customMethod.querySelectorAll('"+selector+"', "+new Gson().toJson(attrs)+","+not+").length");
		
		Element[] elements = new Element[qnt];
		int[] uids = new int[qnt];
		for (int i = -1; ++i < qnt;)
			uids[i] = DOMHandle.getUID(elements[i] = new Element(owner.window));
		
		DOMHandle.registerReturnByCommand(owner, uids, "@customMethod.querySelectorAll", selector, attrs, not);
		
		return elements;
	}
}
