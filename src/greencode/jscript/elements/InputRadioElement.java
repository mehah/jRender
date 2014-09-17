package greencode.jscript.elements;

import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class InputRadioElement extends InputElementCheckable {
	protected InputRadioElement(Window window) { super("radio", window); }
	
	public static InputRadioElement cast(Element e) { return ElementHandle.cast(e, InputRadioElement.class); }
}
