package greencode.jscript.event.custom;

import greencode.jscript.DOMHandle;
import greencode.jscript.Element;
import greencode.jscript.ElementHandle;
import greencode.jscript.Window;

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
