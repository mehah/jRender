package greencode.jscript.dom.event.custom;

import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class CustomEventObject extends DOM {
	protected CustomEventObject(Window window) { super(window); }

	transient Element mainElement;
	
	public Element getMainElement() {
		if(mainElement == null)
			DOMHandle.registerElementByProperty(this, mainElement = ElementHandle.getInstance(window), "mainElement");
		
		return mainElement;
	}
}
