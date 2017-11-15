package com.jrender.jscript.dom.event;

import com.jrender.jscript.DOM;
import com.jrender.jscript.DOMHandle;
import com.jrender.jscript.dom.Element;
import com.jrender.jscript.dom.ElementHandle;
import com.jrender.jscript.dom.Window;

public class EventObject extends DOM {
	protected EventObject(Window window) { super(window); }

	private transient Element target, relatedTarget;
	public final Integer pageX = 0, pageY = 0, which = 0;
	public final Boolean metaKey = false;
	public final String type = null;
	public final Long timeStamp = 0L;
	
	public Element getTarget() {
		if(target == null)
			DOMHandle.registerReturnByProperty(target = ElementHandle.getInstance(window), this, "target");
		
		return target;
	}
	
	public Element getRelatedTarget() {
		if(relatedTarget == null)
			DOMHandle.registerReturnByProperty(relatedTarget = ElementHandle.getInstance(window), this, "relatedTarget");
		
		return relatedTarget;
	}
}
