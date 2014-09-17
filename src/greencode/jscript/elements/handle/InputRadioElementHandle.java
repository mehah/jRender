package greencode.jscript.elements.handle;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.elements.InputRadioElement;


public final class InputRadioElementHandle {
	public static void checkByValue(Element[] es, String value) {
		for (int i = -1; ++i < es.length;) {
			final Element e = es[i];
			final InputRadioElement r = e instanceof InputRadioElement ? (InputRadioElement) e : ElementHandle.cast(e, InputRadioElement.class);
			es[i] = r;
			
			if(r.value().equals(value)) {
				r.checked(true);
				return;
			}
		}
	}
	
	public static InputRadioElement checkByValue(Element mainElement, String name, String value) {
		InputRadioElement r = ElementHandle.cast(mainElement.querySelector("input[type=\"radio\"][name=\""+name+"\"][value=\""+value+"\"]"), InputRadioElement.class);
		r.checked(true);		
		return r;
	}
}
