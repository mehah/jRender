package greencode.jscript;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.google.gson.Gson;

import greencode.kernel.LogMessage;
import greencode.util.GenericReflection;

public final class ElementHandle {
	public static Element getInstance(Window window) { return new Element(window); }
	public static<E extends Element> E getInstance(Class<E> clazz, Window window) {
		return GenericReflection.NoThrow.newInstance(clazz, new Class<?>[]{Window.class}, window);
	}
	
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
			
			E e = getInstance(castTo, element.window);;
		
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
		DOMHandle.CustomMethod.call(e, "empty");
	}
	
	public static Element getOrCreateElementByTagName(Element owner, String tagName) {
		Element e = new Element(owner.window);		
		DOMHandle.CustomMethod.registerElement(owner, e, "getOrCreateElementByTagName", tagName);		
		return e;
	}
	
	public static Element querySelector(Element owner, String selector, HashMap<String, String[]> cssAttrs, boolean not) {
		Element e = new Element(owner.window);		
		DOMHandle.registerElementByCommand(owner, e, "@customMethod.querySelector", selector, cssAttrs, not);		
		return e;
	}
	
	public static Element[] querySelectorAll(Element owner, String selector, HashMap<String, String[]> cssAttrs, boolean not) {
		final int qnt = DOMHandle.getVariableValueByPropertyNoCache(owner, "querySelectorAll.length", Integer.class, "@customMethod.querySelectorAll('"+selector+"', "+new Gson().toJson(cssAttrs)+","+not+").length");
		
		Element[] elements = new Element[qnt];
		int[] uids = new int[qnt];
		for (int i = -1; ++i < qnt;)
			uids[i] = DOMHandle.getUID(elements[i] = new Element(owner.window));
		
		DOMHandle.registerReturnByCommand(owner, uids, "@customMethod.querySelectorAll", selector, cssAttrs, not);
		
		return elements;
	}
	
	public static Element querySelector(Element owner, String selector, String javascriptSyntax) {
		Element e = new Element(owner.window);		
		DOMHandle.registerElementByCommand(owner, e, "@customMethod.querySelector", selector, javascriptSyntax);		
		return e;
	}
	
	public static Element[] querySelectorAll(Element owner, String selector, String javascriptSyntax) {
		final int qnt = DOMHandle.getVariableValueByPropertyNoCache(owner, "querySelectorAll.length", Integer.class, "@customMethod.querySelectorAll('"+selector+"', '"+javascriptSyntax+"').length");
		
		Element[] elements = new Element[qnt];
		int[] uids = new int[qnt];
		for (int i = -1; ++i < qnt;)
			uids[i] = DOMHandle.getUID(elements[i] = new Element(owner.window));
		
		DOMHandle.registerReturnByCommand(owner, uids, "@customMethod.querySelectorAll", selector, javascriptSyntax);
		
		return elements;
	}
	
	public static void addClass(Element e, String className) {
		DOMHandle.CustomMethod.call(e, "addClass", className);
	}
	
	public static void removeClass(Element e, String className) {
		DOMHandle.CustomMethod.call(e, "removeClass", className);
	}
	
	public static Node prepend(Element e, Node node) {
		DOMHandle.CustomMethod.call(e, "prepend", node);
		return node;
	}
}
