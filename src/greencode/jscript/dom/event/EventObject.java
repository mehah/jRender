package greencode.jscript.dom.event;

import greencode.jscript.DOM;
import greencode.jscript.DOMHandle;
import greencode.jscript.dom.Element;
import greencode.jscript.dom.ElementHandle;
import greencode.jscript.dom.Window;

public class EventObject extends DOM {
	protected EventObject(Window window) { super(window); }

	private transient Element target, relatedTarget;
	public final Integer pageX = 0, pageY = 0, which = 0;
	public final Boolean metaKey = false;
	public final String type = null;
	public final Long timeStamp = 0L;
	
	public Element getTarget() {
		if(target == null)
			DOMHandle.registerElementByProperty(this, target = ElementHandle.getInstance(window), "target");
		
		return target;
	}
	
	public Element getRelatedTarget() {
		if(relatedTarget == null)
			DOMHandle.registerElementByProperty(this, relatedTarget = ElementHandle.getInstance(window), "relatedTarget");
		
		return relatedTarget;
	}
}
