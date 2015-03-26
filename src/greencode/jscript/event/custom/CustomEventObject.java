package greencode.jscript.event.custom;

import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

public class CustomEventObject extends DOM {
	protected CustomEventObject(Window window) { super(window); }

	transient Element mainElement;
	
	public Element getMainElement() {
		if(mainElement == null)
			DOMHandle.registerElementByProperty(this, mainElement = ElementHandle.getInstance(window), "mainElement");
		
		return mainElement;
	}
}
