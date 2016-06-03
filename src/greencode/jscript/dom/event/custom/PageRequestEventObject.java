package greencode.jscript.dom.event.custom;

import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public abstract class PageRequestEventObject extends CustomEventObject {
	protected PageRequestEventObject(Window window) { super(window); }

	private transient Element target;
	public final Boolean empty = false, changeURL = false, keepViewId = false;
	public final String appendToSelector = null, href = null;
	
	public Element getAppendToElement() {
		return getMainElement();
	}
	
	public Element getTarget() {
		if(target == null)
			DOMHandle.registerElementByProperty(this, target = ElementHandle.getInstance(window), "target");
		
		return target;
	}
}
