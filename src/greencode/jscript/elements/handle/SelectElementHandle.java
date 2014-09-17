package greencode.jscript.elements.handle;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;


public final class SelectElementHandle {
	public static void selectOptionByValue(Element e, String value) {
		DOMHandle.execCommand(e, "customMethod.selectOptionByValue", value);
	}
	
	public static void selectOptionByText(Element e, String text) {
		DOMHandle.execCommand(e, "customMethod.selectOptionByText", text);
	}
}
