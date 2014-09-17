package greencode.jscript.elements.handle;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.elements.InputCheckboxElement;


public final class InputCheckboxElementHandle {
	public static void checkByValue(Element[] es, String value) {
		for (int i = -1; ++i < es.length;) {
			final Element e = es[i];
			final InputCheckboxElement c = e instanceof InputCheckboxElement ? (InputCheckboxElement) e : ElementHandle.cast(e, InputCheckboxElement.class);
			es[i] = c;
			
			if(c.value().equals(value)) {
				c.checked(true);
				return;
			}
		}
	}
	
	public static InputCheckboxElement checkByValue(Element mainElement, String name, String value) {
		InputCheckboxElement c = ElementHandle.cast(mainElement.querySelector("input[type=\"checkbox\"][name=\""+name+"\"][value=\""+value+"\"]"), InputCheckboxElement.class);
		c.checked(true);		
		return c;
	}
	
	public static InputCheckboxElement[] checkByValues(Element mainElement, String name, String[] value) {
		StringBuilder s = new StringBuilder();
		for (String v : value) {
			if(s.length() > 0) s.append(",");			
			s.append("input[type=\"checkbox\"][name=\""+name+"\"][value=\""+v+"\"]");
		}
		
		Element[] elements = mainElement.querySelectorAll(s.toString());
		
		InputCheckboxElement[] inputs = new InputCheckboxElement[elements.length];
		
		for (int i = -1; ++i < elements.length;) {			
			final InputCheckboxElement c = ElementHandle.cast(elements[i], InputCheckboxElement.class);
			c.checked(true);
			
			elements[i] = c;
		}
		
		return inputs;
	}
}
